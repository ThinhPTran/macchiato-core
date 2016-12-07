(ns macchiato.test.anti-forgery
  (:require
    [macchiato.middleware.anti-forgery :as af]
    [macchiato.test.mock.request :refer [header request]]
    [macchiato.test.mock.util :refer [mock-handler ok-response]]
    [cljs.test :refer-macros [is are deftest testing use-fixtures]]))

(deftest forgery-protection-test
  (let [response {:status 200, :headers {}, :body "Foo"}
        handler  (mock-handler af/wrap-anti-forgery (ok-response response))]
    (are [status req] (= (:status (handler req)) status)
                      403 (-> (request :post "/")
                              (assoc :form-params {"__anti-forgery-token" "foo"}))
                      403 (-> (request :post "/")
                              (assoc :session {::af/anti-forgery-token "foo"})
                              (assoc :form-params {"__anti-forgery-token" "bar"}))
                      200 (-> (request :post "/")
                              (assoc :session {::af/anti-forgery-token "foo"})
                              (assoc :form-params {"__anti-forgery-token" "foo"})))))


