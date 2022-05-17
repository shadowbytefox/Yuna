(ns yuna.sql.sql-wraper
  (:require [hugsql.core :as hugsql]
            [clojure.data.json :as js]
            [postgre-types.json :refer [add-jsonb-type]]))
(add-jsonb-type js/write-str js/read-str)

(hugsql/def-db-fns "sql/login.sql")
(hugsql/def-db-fns "sql/register.sql")