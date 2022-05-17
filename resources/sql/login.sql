------ Login Querys

-- :name login
-- :doc User Login
SELECT username,id,language,identity FROM profile
WHERE LOWER(email) = LOWER(:email) AND password = :password AND role = :role;

-- :name if-verification-done?
SELECT verification FROM profile
WHERE LOWER(email) = LOWER(:email) AND password = :password AND verification = true

-- :name check-code-if-verification-done
SELECT verification from profile
WHERE verification_code = :verification_code AND verification = true

-- :name verification-a-email :! :n
UPDATE profile
SET verification= true, verification_code = NULL
WHERE verification_code = :verification_code AND verification = false;

-- :name valid-changeCode
SELECT changeCode FROM profile
WHERE (changeCode = :changeCode AND id = :user_id)

-- :name valid-access-sql
SELECT viewCode FROM profile
WHERE id = :id AND viewCode = :viewCode AND viewCode != '';


------ JWT Querys

-- :name save-jwt-token :! :n
UPDATE profile
	SET jwt= :jwt_token
	WHERE id = :user_id;

-- :name check-if-same-jwt
SELECT id, jwt
from profile
WHERE id = :user_id and jwt = :jwt_token;