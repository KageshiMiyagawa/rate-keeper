package jp.co.ratekeeper.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jp.co.ratekeeper.ApplicationConstants;
import jp.co.ratekeeper.code.SortType;
import jp.co.ratekeeper.model.GameResultFetchCond;
import jp.co.ratekeeper.model.table.TtGameResult;
import jp.co.ratekeeper.util.DateTimeUtil;

@Repository
public class GameResultManageRepository {

	@Autowired
	private GameResultCrudRepository gameResultCrudRepository;
	@PersistenceContext
	private EntityManager entityManager;

	public TtGameResult find(Integer gameId) {
		return gameResultCrudRepository.getReferenceById(gameId);
	}
	
	public List<TtGameResult> findAll() {
		return gameResultCrudRepository.findAll();
	}
	
	public List<TtGameResult> findGameResultByFetchCond(GameResultFetchCond gameResultFetchCond) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TtGameResult> cq = cb.createQuery(TtGameResult.class);
		Root<TtGameResult> root = cq.from(TtGameResult.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(gameResultFetchCond.getWinnerId())) {
			predicates.add(cb.equal(root.get("winnerId"), gameResultFetchCond.getWinnerId()));
		}

		if (StringUtils.isNotBlank(gameResultFetchCond.getLoserId())) {
			predicates.add(cb.equal(root.get("loserId"), gameResultFetchCond.getLoserId()));
		}

		if (StringUtils.isNotBlank(gameResultFetchCond.getSyncFlg())) {
			predicates.add(cb.equal(root.get("syncFlg"), gameResultFetchCond.getSyncFlg()));
		}
		
		if (StringUtils.isNotBlank(gameResultFetchCond.getStartDate()) && StringUtils.isNotBlank(gameResultFetchCond.getEndDate())) {
			predicates.add(cb.between(root.get("gameDate"), gameResultFetchCond.getStartDate(), gameResultFetchCond.getEndDate()));
		} else {
			if (StringUtils.isNotBlank(gameResultFetchCond.getStartDate())) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("gameDate"), gameResultFetchCond.getStartDate()));
			}
			if (StringUtils.isNotBlank(gameResultFetchCond.getEndDate())) {
				predicates.add(cb.lessThanOrEqualTo(root.get("gameDate"), gameResultFetchCond.getEndDate()));
			}
			
		}
		cq.where(predicates.toArray(new Predicate[predicates.size()]));

		if(StringUtils.isNotEmpty(gameResultFetchCond.getSortType())) {
			if (SortType.ASC.getCode().equals(gameResultFetchCond.getSortType())) {
				cq.orderBy(cb.asc(root.get("gameDate")));
			} else {
				cq.orderBy(cb.desc(root.get("gameDate")));
			}
		}
		
		TypedQuery<TtGameResult> query = entityManager.createQuery(cq);

		return query.getResultList();
	}
	
	public List<TtGameResult> findGameResultNotCalculated() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TtGameResult> cq = cb.createQuery(TtGameResult.class);
		Root<TtGameResult> root = cq.from(TtGameResult.class);
		cq.where(cb.equal(root.get("syncFlg"), "0"));
		cq.orderBy(cb.asc(root.get("gameDate")));
		
		return entityManager.createQuery(cq).getResultList();
	}

	public TtGameResult save(TtGameResult game) {
		return gameResultCrudRepository.save(game);
	}
	
	public List<TtGameResult> saveAll(List<TtGameResult> gameList) {
		return gameResultCrudRepository.saveAll(gameList);
	}

	public TtGameResult saveScanGameResult (int userId) {
		TtGameResult gameResult = findUnsettledGameResult();
		String nowDate = DateTimeUtil.getNowDateStr(ApplicationConstants.DATETIME_FORMAT);
		if (Objects.isNull(gameResult)) {
			// 未完了の対局データが存在しない場合は新規登録
			gameResult = new TtGameResult();
			gameResult.setWinnerId(userId);
			gameResult.setSyncFlg("0");
			gameResult.setGameDate(nowDate);
		} else {
			// 未完了の対局データが存在する場合は更新
			gameResult.setLoserId(userId);
		}
		gameResult.setUpdateDate(nowDate);
		return gameResultCrudRepository.save(gameResult);
	}
	
	public void deleteGameResult(Integer gameId) {
		gameResultCrudRepository.deleteById(gameId);
	}
	
	private TtGameResult findUnsettledGameResult() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TtGameResult> cq = cb.createQuery(TtGameResult.class);
		Root<TtGameResult> root = cq.from(TtGameResult.class);
		cq.where(cb.isNull(root.get("loserId")));
		
		List<TtGameResult> resultList = entityManager.createQuery(cq).getResultList();
		if (CollectionUtils.isEmpty(resultList)) {
			return null;
		}
		// TODO 未確定の対局が2件以上あった場合の考慮
		return resultList.get(0);
	}
}
