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
@Table(name = "tt_game_result")
public class TtGameResult {
	@Id
	@Column(name = "game_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer gameId;

	@Column(name = "game_date")
	private String gameDate;
	
	@Column(name = "winner_id")
	private Integer winnerId;

	@Column(name = "loser_id")
	private Integer loserId;

	@Column(name = "floating_rate")
	private Integer floatingRate;
	
	@Column(name = "sync_flg")
	private String syncFlg;
	
	@Column(name = "update_date")
	private String updateDate;

}