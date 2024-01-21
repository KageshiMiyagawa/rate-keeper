package jp.co.ratekeeper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.ratekeeper.model.table.TtGameResult;

public interface GameResultCrudRepository extends JpaRepository<TtGameResult, Integer> {

}
