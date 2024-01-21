package jp.co.ratekeeper.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;

import jp.co.ratekeeper.ApplicationConstants;
import jp.co.ratekeeper.model.GameResultData;
import jp.co.ratekeeper.model.GameResultFetchCond;
import jp.co.ratekeeper.model.table.TtGameResult;
import jp.co.ratekeeper.repository.GameResultManageRepository;
import jp.co.ratekeeper.repository.UserManageRepository;
import jp.co.ratekeeper.util.DateTimeUtil;

@Service
public class GameResultManageService {

	@Autowired
	private GameResultManageRepository gameResultManageRepository;
	@Autowired
	private UserManageRepository userManageRepository;
	@Autowired
	private CalcRateService calcRateService;

	@Transactional(readOnly = true)
	public List<GameResultData> find(GameResultFetchCond gameResultFetchCond) throws Exception {
		if (StringUtils.isNotEmpty(gameResultFetchCond.getStartDate())) {
			gameResultFetchCond.setStartDate(
					DateTimeUtil.convertDateStrFormat(gameResultFetchCond.getStartDate(),
							ApplicationConstants.DATETIME_FORMAT_FOR_INPUT, ApplicationConstants.DATETIME_FORMAT));
		}
		if (StringUtils.isNotEmpty(gameResultFetchCond.getEndDate())) {
			gameResultFetchCond.setStartDate(
					DateTimeUtil.convertDateStrFormat(gameResultFetchCond.getEndDate(),
							ApplicationConstants.DATETIME_FORMAT_FOR_INPUT, ApplicationConstants.DATETIME_FORMAT));
		}
		
		List<TtGameResult> gameResultList = gameResultManageRepository.findGameResultByFetchCond(gameResultFetchCond);
		return gameResultList.stream().map(o -> convertGameResultDataForDisp(o)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public GameResultData find(Integer gameId) {
		// TODO getByRefrenceIdが機能しないため、暫定
//		TtGameResult ttGameResult = gameResultManageRepository.find(gameId);
		TtGameResult ttGameResult = gameResultManageRepository.findAll().stream()
				.filter(o -> o.getGameId().equals(gameId))
				.findFirst().orElse(null);
		return convertGameResultDataForDisp(ttGameResult);
	}
	
	@Transactional
	public GameResultData save(GameResultData gameResultData) {
		TtGameResult gameResult = convertGameResultDataForRepository(gameResultData);
		TtGameResult savedGameResult = gameResultManageRepository.save(gameResult);
		calcRateService.restoreGameResultToPreCalcState(gameResult.getGameDate());
		return convertGameResultDataForDisp(savedGameResult);
	}
	
	@Transactional
	public GameResultData saveScanGameResult(String userId) {
		TtGameResult gameResult = gameResultManageRepository.saveScanGameResult(Integer.parseInt(userId));
		return convertGameResultDataForDisp(gameResult);
	}

	@Transactional
	public List<GameResultData> saveCsvFileData(MultipartFile file) throws Exception {
		List<TtGameResult> gameResultList = new ArrayList<>();
		String nowDate = DateTimeUtil.getNowDateStr(ApplicationConstants.DATETIME_FORMAT);
		try (CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(file.getInputStream())))) {
			List<String[]> lines = csvReader.readAll();
			
			boolean isHeader = true;
			for (String[] line : lines) {
				if (isHeader) {
					isHeader = false;
					continue;
				}
				TtGameResult gameResult = new TtGameResult();
				gameResult.setGameDate(line[0]);
				gameResult.setWinnerId(Integer.valueOf(line[1]));
				gameResult.setLoserId(Integer.valueOf(line[2]));
				gameResult.setFloatingRate(Integer.valueOf(line[3]));
				gameResult.setSyncFlg(line[4]);
				gameResult.setUpdateDate(nowDate);
				gameResultList.add(gameResult);
			}
		}
		gameResultList = gameResultManageRepository.saveAll(gameResultList);
		return gameResultList.stream().map(o -> convertGameResultDataForDisp(o)).collect(Collectors.toList());
	}
	
	@Transactional
	public void deleteGameResult(Integer gameId) {
		gameResultManageRepository.deleteGameResult(gameId);
	}

	private TtGameResult convertGameResultDataForRepository (GameResultData gameResultData) {
		TtGameResult gameResult = new TtGameResult();
		BeanUtils.copyProperties(gameResultData, gameResult);
		String nowDate = DateTimeUtil.getNowDateStr(ApplicationConstants.DATETIME_FORMAT);

		if (Objects.nonNull(gameResultData.getWinner())) {
			gameResult.setWinnerId(Integer.parseInt(gameResultData.getWinner()));
		}
		
		if (Objects.nonNull(gameResultData.getLoser())) {
			gameResult.setLoserId(Integer.parseInt(gameResultData.getLoser()));
		}
		
		if (StringUtils.isEmpty(gameResult.getGameDate())) {
			gameResult.setGameDate(nowDate);
		} else {
			String gameDate = DateTimeUtil.convertDateStrFormat(gameResult.getGameDate(), ApplicationConstants.DATETIME_FORMAT_FOR_INPUT_2,
					ApplicationConstants.DATETIME_FORMAT);
			gameResult.setGameDate(gameDate);
		}
		
		if (StringUtils.isEmpty(gameResult.getSyncFlg())) {
			gameResult.setSyncFlg("0");
		}
		
		if (StringUtils.isEmpty(gameResult.getUpdateDate())) {
			gameResult.setUpdateDate(nowDate);
		}
		
		return gameResult;
	}
	
	private GameResultData convertGameResultDataForDisp(TtGameResult gameResult) {
		GameResultData gameResultData = new GameResultData();
		BeanUtils.copyProperties(gameResult, gameResultData);
		// TODO DBアクセスを1回で済ませるようにする
		if(Objects.nonNull(gameResult.getWinnerId())) {
			String winner = userManageRepository.find(gameResult.getWinnerId()).getUserName();
			gameResultData.setWinner(winner);
		}
		if(Objects.nonNull(gameResult.getLoserId())) {
			String loser = userManageRepository.find(gameResult.getLoserId()).getUserName();
			gameResultData.setLoser(loser);
		}
		if (StringUtils.isNotEmpty(gameResult.getGameDate())) {
			String gameDate = DateTimeUtil.convertDateStrFormat(gameResult.getGameDate(),
					ApplicationConstants.DATETIME_FORMAT, ApplicationConstants.DATETIME_FORMAT_FOR_DISP);
			gameResultData.setGameDate(gameDate);
		}
		return gameResultData;
	}
	
}
