
-- :name account-info-by-account :? :1
-- :doc Get account info by given account
select account_id, account, parent_account_id, owner_chain_type, owner, manager_chain_type, manager, status, enable_sub_account, renew_sub_account_price, nonce, registered_at, expired_at, created_at, updated_at
from t_account_info
where account = :account

-- :name records-by-account :? :*
-- :doc Get records related to given account
select account_id, account, `key`, `value`, `label`, `type`, created_at, updated_at
from t_records_info
where account = :account

-- :name inviter-info-by-invitee-account :? :*
select id, inviter_id, inviter_account, inviter_chain_type, inviter_address, service_type, reward_type, reward, `action`, created_at, updated_at
from t_rebate_info
where invitee_account = :invitee_account

-- :name trade-deal-info-by-account :? :*
select id, account_id, account, deal_type, sell_chain_type, sell_address, buy_chain_type, buy_address, price_ckb, price_usd, created_at, updated_at
from t_trade_deal_info
where account = :account
