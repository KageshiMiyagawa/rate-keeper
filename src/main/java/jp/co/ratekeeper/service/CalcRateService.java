package jp.co.ratekeeper.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.ratekeeper.ApplicationConstants;
import jp.co.ratekeeper.code.SortType;
import jp.co.ratekeeper.model.CalcResultData;
import jp.co.ratekeeper.model.CalcResultGameData;
import jp.co.ratekeeper.model.CalcResultUserData;
import jp.co.ratekeeper.model.GameResultFetchCond;
import jp.co.ratekeeper.model.table.TtGameResult;
import jp.co.ratekeeper.model.table.TtUser;
import jp.co.ratekeeper.repository.GameResultManageRepository;
import jp.co.ratekeeper.repository.UserManageRepository;
import jp.co.ratekeeper.util.DateTimeUtil;

@Service
public class CalcRateService {

	@Autowired
	GameResultManageRepository gameResultManageRepository;
	@Autowired
	UserManageRepository userManageRepository;

	/** K値 */
	private static final Integer K_FACTOR = 16;
	private static final Integer RESULT_WIN = 1;
	private static final Integer RESULT_LOSE = 0;

	@Transactional
	public CalcResultData calcRate() throws Exception {
		// set up
		List<TtGameResult> notCalculatedGameList = gameResultManageRepository.findGameResultNotCalculated();
		List<TtUser> calcTargetUsers = createUserListFromGameResultList(notCalculatedGameList);
		Map<Integer, TtUser> calcTargetUserMap = calcTargetUsers.stream()
				.collect(Collectors.toMap(TtUser::getUserId, o -> o));

		// back up before calc data
		List<TtUser> calcTargetUsersBeforeCalcRate = new ArrayList<>();
		for (TtUser calcTargetUser : calcTargetUsers) {
			TtUser calcTargetUserBeforeCalcRate = new TtUser();
			BeanUtils.copyProperties(calcTargetUserBeforeCalcRate, calcTargetUser);
			calcTargetUsersBeforeCalcRate.add(calcTargetUserBeforeCalcRate);
		}
		Map<Integer, TtUser> calcTargetUserMapBeforeCalcRate = calcTargetUsersBeforeCalcRate.stream()
				.collect(Collectors.toMap(TtUser::getUserId, o -> o));

		// execute calc
		List<CalcResultGameData> calcResultGameData = executeCalc(notCalculatedGameList, calcTargetUserMap);
		// generate disp data
		List<TtUser> calcTargetUsersAfterCalcRate = calcTargetUserMap.values().stream().collect(Collectors.toList());
		List<CalcResultUserData> calcResultUserData = generateCalcResultData(calcTargetUsersAfterCalcRate,
				calcTargetUserMapBeforeCalcRate);
		
		return new CalcResultData(calcResultUserData, calcResultGameData);
	}
	
	public int restoreGameResultToPreCalcState(String gameDate) {
		// get restore target games
		GameResultFetchCond fetchCond = new GameResultFetchCond();
		fetchCond.setStartDate(gameDate);
		fetchCond.setSyncFlg("1");
		fetchCond.setSortType(SortType.ASC.getCode());
		List<TtGameResult> restoreTargetGames = gameResultManageRepository.findGameResultByFetchCond(fetchCond);
		if (CollectionUtils.isEmpty(restoreTargetGames)) {
			return 0;
		}
		// get restore target users
		Map<Integer, TtUser> restoreTargetUserMap = createUserListFromGameResultList(restoreTargetGames).stream()
				.collect(Collectors.toMap(TtUser::getUserId, o -> o));
		// exec restore calc rate
		for (TtGameResult restoreTargetGame : restoreTargetGames) {
			TtUser winner = restoreTargetUserMap.get(restoreTargetGame.getWinnerId());
			TtUser loser = restoreTargetUserMap.get(restoreTargetGame.getLoserId());
			winner.setRate(winner.getRate() - restoreTargetGame.getFloatingRate());
			loser.setRate(loser.getRate() + restoreTargetGame.getFloatingRate());
			restoreTargetGame.setFloatingRate(null);
			restoreTargetGame.setSyncFlg("0");
		}		
		List<TtUser> restoreTargetUsers = restoreTargetUserMap.values().stream().collect(Collectors.toList());
		userManageRepository.saveAll(restoreTargetUsers);
		// and restore game result sync flg
		gameResultManageRepository.saveAll(restoreTargetGames);
		
		return restoreTargetGames.size();
	}
	
	private List<TtUser> createUserListFromGameResultList (List<TtGameResult> gameResultList) {
		List<Integer> calcTargetUserIds = new ArrayList<>();
		for (TtGameResult gameResult : gameResultList) {
			calcTargetUserIds.add(gameResult.getWinnerId());
			calcTargetUserIds.add(gameResult.getLoserId());
		}
		calcTargetUserIds = calcTargetUserIds.stream().distinct().collect(Collectors.toList());
		List<TtUser> calcTargetUsers = userManageRepository.findAllByIds(calcTargetUserIds);
		return calcTargetUsers;
	}
	
	private List<CalcResultGameData> executeCalc(List<TtGameResult> notCalculatedGameList,
			Map<Integer, TtUser> calcTargetUserMap) {
		List<CalcResultGameData> calcResultGameDataList = new ArrayList<>();
		// レーティング計算を実施
		for (TtGameResult gameResult : notCalculatedGameList) {
			TtUser winner = calcTargetUserMap.get(gameResult.getWinnerId());
			TtUser loser = calcTargetUserMap.get(gameResult.getLoserId());

			double expectedScoreWinner = getExpectedScore(winner.getRate(), loser.getRate());
			double expectedScoreLoser = getExpectedScore(loser.getRate(), winner.getRate());

			int beforeRateWinner = winner.getRate();
			int beforeRateLoser = loser.getRate();
			int afterRateWinner = updateRating(beforeRateWinner, expectedScoreWinner, K_FACTOR, RESULT_WIN);
			int afterRateLoser = updateRating(beforeRateLoser, expectedScoreLoser, K_FACTOR, RESULT_LOSE);

			winner.setRate(afterRateWinner);
			loser.setRate(afterRateLoser);

			userManageRepository.updateAndFlush(winner);
			userManageRepository.updateAndFlush(loser);
			
			int floatingRate = afterRateWinner - beforeRateWinner;
			gameResult.setFloatingRate(floatingRate);

			CalcResultGameData calcResultGameData = new CalcResultGameData();
			calcResultGameData.setGameId(gameResult.getGameId());
			calcResultGameData.setGameDate(gameResult.getGameDate());
			calcResultGameData.setWinner(calcTargetUserMap.get(gameResult.getWinnerId()).getUserName());
			calcResultGameData.setWinnerRateBefore(beforeRateWinner);
			calcResultGameData.setWinnerRateAfter(afterRateWinner);
			calcResultGameData.setLoser(calcTargetUserMap.get(gameResult.getLoserId()).getUserName());
			calcResultGameData.setLoserRateBefore(beforeRateLoser);
			calcResultGameData.setLoserRateAfter(afterRateLoser);
			calcResultGameDataList.add(calcResultGameData);
		}

		// 計算済フラグを更新
		String nowDate = DateTimeUtil.getNowDateStr(ApplicationConstants.DATETIME_FORMAT);
		for (TtGameResult gameResult : notCalculatedGameList) {
			gameResult.setSyncFlg("1");
			gameResult.setUpdateDate(nowDate);
		}
		gameResultManageRepository.saveAll(notCalculatedGameList);

		return calcResultGameDataList;
	}

	private List<CalcResultUserData> generateCalcResultData(List<TtUser> calcTargetUsersAfterCalcRate,
			Map<Integer, TtUser> calcTargetUserMapBeforeCalcRate) {
		List<CalcResultUserData> calcResultUserDataList = new ArrayList<>();

		for (TtUser userAfterCalcRate : calcTargetUsersAfterCalcRate) {
			CalcResultUserData calcResultUserData = new CalcResultUserData();
			TtUser userBeforeCalcRate = calcTargetUserMapBeforeCalcRate.get(userAfterCalcRate.getUserId());
			calcResultUserData.setUserId(userAfterCalcRate.getUserId());
			calcResultUserData.setUserName(userAfterCalcRate.getUserName());
			calcResultUserData.setBeforeRate(userBeforeCalcRate.getRate());
			calcResultUserData.setAfterRate(userAfterCalcRate.getRate());

			int riseAndFall = calcResultUserData.getAfterRate() - calcResultUserData.getBeforeRate();
			String raiseAndFallStr = String.valueOf(riseAndFall);
			if (riseAndFall > 0) {
				raiseAndFallStr = "+" + raiseAndFallStr;
			} else if (riseAndFall == 0) {
				raiseAndFallStr = "±" + raiseAndFallStr;
			}
			calcResultUserData.setRiseAndFall(raiseAndFallStr);

			calcResultUserDataList.add(calcResultUserData);
		}

		return calcResultUserDataList;
	}

	// 実力差に応じたスコアの期待値を計算する関数
	private double getExpectedScore(int rating1, int rating2) {
		double expectedScore = 1.0 / (1.0 + Math.pow(10.0, (rating2 - rating1) / 400.0));
		return expectedScore;
	}

	// レーティングを更新する関数
	private int updateRating(int rating, double score, double kFactor, int result) {
		int newRating = (int) Math.round(rating + kFactor * (result - score));
		return newRating;
	}

}
