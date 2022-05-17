-- :name check-email-exist
SELECT id
FROM profile
WHERE email = :email;

-- :name create-account :<!
INSERT INTO profile(
password, email, username, viewcode, changecode, verification_code, role)
VALUES (:password, :email, :username, :viewcode, :changecode, :verification_code, :role) returning id

-- :name get-user-email
SELECT email
FROM profile
WHERE id = :user_id;

-- :name set-new-changecode :! :n
UPDATE profile
SET changecode= :changecode
WHERE id = :user_id;