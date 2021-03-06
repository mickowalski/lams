SET AUTOCOMMIT = 0;
SET FOREIGN_KEY_CHECKS = 0;

--  LDEV-3382: Add datetime parameter check to LoginRequest
ALTER TABLE lams_ext_server_org_map ADD COLUMN time_to_live_login_request_enabled TINYINT(1) NOT NULL DEFAULT 0;

COMMIT;
SET AUTOCOMMIT = 1;
SET FOREIGN_KEY_CHECKS = 1;