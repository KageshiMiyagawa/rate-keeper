package jp.co.ratekeeper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.ratekeeper.model.table.TtUser;

public interface UserManageCrudRepository extends JpaRepository<TtUser, Integer> {

}
