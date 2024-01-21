package jp.co.ratekeeper.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jp.co.ratekeeper.code.SortType;
import jp.co.ratekeeper.model.UserFetchCond;
import jp.co.ratekeeper.model.table.TtUser;

@Repository
public class UserManageRepository {

	@Autowired
	private UserManageCrudRepository userManageCrudRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public TtUser find(Integer userId) {
		return userManageCrudRepository.getReferenceById(userId);
	}
	
	public List<TtUser> findAll() {
		return userManageCrudRepository.findAll();
	}
	
	public List<TtUser> findAllByIds(List<Integer> ids) {
		return userManageCrudRepository.findAllById(ids);
	}

	public List<TtUser> findUserByFetchCond(UserFetchCond userFetchCond) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TtUser> cq = cb.createQuery(TtUser.class);
		Root<TtUser> root = cq.from(TtUser.class);

		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(userFetchCond.getUserType())) {
			predicates.add(cb.equal(root.get("userType"), userFetchCond.getUserType()));
		}

		if (StringUtils.isNotBlank(userFetchCond.getUserName())) {
			predicates.add(cb.equal(root.get("userName"), userFetchCond.getUserName()));
		}

		if (StringUtils.isNotBlank(userFetchCond.getGrade())) {
			predicates.add(cb.equal(root.get("grade"), userFetchCond.getGrade()));
		}
		
		if (StringUtils.isNotBlank(userFetchCond.getJoinStartDate()) && StringUtils.isNotBlank(userFetchCond.getJoinEndDate())) {
			predicates.add(cb.between(root.get("joinDate"), userFetchCond.getJoinStartDate(), userFetchCond.getJoinEndDate()));
		} else {
			if (StringUtils.isNotBlank(userFetchCond.getJoinStartDate())) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("joinDate"), userFetchCond.getJoinStartDate()));
			}
			if (StringUtils.isNotBlank(userFetchCond.getJoinEndDate())) {
				predicates.add(cb.lessThanOrEqualTo(root.get("joinDate"), userFetchCond.getJoinEndDate()));
			}
			
		}
		cq.where(predicates.toArray(new Predicate[predicates.size()]));
		
		if(StringUtils.isNotEmpty(userFetchCond.getSortItem())) {
			if (SortType.ASC.getCode().equals(userFetchCond.getSortType())) {
				cq.orderBy(cb.asc(root.get(userFetchCond.getSortItem())));
			} else {
				cq.orderBy(cb.desc(root.get(userFetchCond.getSortItem())));
			}
		}

		TypedQuery<TtUser> query = entityManager.createQuery(cq);

		return query.getResultList();
	}
	
	public TtUser save(TtUser user) {
		return userManageCrudRepository.save(user);
	}
	
	public List<TtUser> saveAll(List<TtUser> userList) {
		return userManageCrudRepository.saveAll(userList);
	}
	
	public void updateAndFlush(TtUser ttUser) {
		userManageCrudRepository.save(ttUser);
		userManageCrudRepository.flush();
	}
		
}
