(ns brand.core-test
  (:use clojure.test
        brand.core))

(deftest test-normalize-data
  (testing "URL should be stripped of http://"
    (is (not (re-find #"^http://" (normalize-data "http://foo.bar.com/quux")))))
  (testing "URL should be stripped of https://"
    (is (not (re-find #"^http://" (normalize-data "https://foo.com/quux/")))))
  (testing "URL should not contain trailing components"
    (is (not (re-find #"/" (normalize-data "http://grunk.com/fleep/bleep"))))))


(deftest test-fuzz
  (testing "Same should yeild a distance of 0.0"
    (is (= 0.0 (fuzz "http://foo.bar.com/quxx" "http://foo.bar.com/quxx"))))
  (testing "A distance over 0.11235955056179775 should return nil"
    (is (nil? (fuzz "http://foo.bar.com/quxx" "http://rob.bar.com/quxx"))))
  (testing "A distance less than 0.11235955056179775 should return non-nil"
    (is (not (nil? (fuzz "http://foo.bar.com/quxx" "http://roo.bar.com/quxx"))))))

;; (deftest test-lookup
;;   (testing "FIXME, I fail."
;;     (is (= 0 1))))

;; (deftest test-authenticated?
;;   (testing "FIXME, I fail."
;;     (is (= 0 1))))

;; (deftest test-get-brand
;;   (testing "FIXME, I fail."
;;     (is (= 0 1))))

;; (deftest test-wrap-error-page
;;   (testing "FIXME, I fail."
;;     (is (= 0 1))))
