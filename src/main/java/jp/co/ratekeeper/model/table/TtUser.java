package jp.co.ratekeeper.model.table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "tt_user")
public class TtUser {
	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;

	@Column(name = "user_type")
	private String userType;

	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "rate")
	private Integer rate;

	@Column(name = "grade")
	private String grade;

	@Column(name = "join_date")
	private String joinDate;

	@Column(name = "update_date")
	private String updateDate;
	
}