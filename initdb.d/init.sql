CREATE TABLE ratekeeper.tt_user(
	user_id INTEGER not null AUTO_INCREMENT PRIMARY KEY comment 'ユーザID', 
	user_type char(1) comment 'ユーザ種別', 
	user_name varchar(20) comment 'ユーザ名', 
	rate INTEGER comment 'レーティング',
	grade char(2) comment '段位', 
	join_date char(14) comment '入会日', 
	update_date char(14) comment '更新日'
)
default charset=utf8mb4
comment='ユーザ';

CREATE TABLE ratekeeper.tt_game_result(
	game_id INTEGER not null AUTO_INCREMENT PRIMARY KEY comment '対局ID',
	game_date char(14) comment '対局日時',
	winner_id INTEGER comment '勝者ID',
	loser_id INTEGER comment '敗者ID',
	floating_rate SMALLINT comment '変動レート',
	sync_flg char(1) comment '同期フラグ',
	update_date char(14) comment '更新日'
)
default charset=utf8mb4
comment='対局結果';