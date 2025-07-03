package com.project.shopapp.ultis;

public class MessageKeys {
    public static final String LOGIN_SUCCESSFULLY = "user.signin.login_successfully";
    public static final String LOGIN_FAILED = "user.signin.login_failed";
    public static final String WRONG_PHONE_NUMBER = "user.signin.wrong_phone_number";
    public static final String WRONG_PASSWORD = "user.signin.wrong_password";

    public static final String NEW_PASSWORD_SAME_AS_CURRENT = "user.new_password_same_as_current";

    public static final String PASSWORD_CHANGE_SUCCESSFULLY = "user.password_change_successfully";
    public static final String REGISTRATION_FAILED = "user.signup.registration_failed";
    public static final String PHONENUMBER_ALREADY_EXISTS = "user.signup.phonenumber_already_exists";
    public static final String REGISTRATION_SUCCESSFULLY = "user.signup.registration_successfully";
    public static final String PASSWORD_NOT_MATCH = "user.signup.password_not_match";
    public static final String CATEGORY_CREATED_SUCCESSFULLY = "category.created.successfully";
    public static final String CATEGORY_GET_LIST_CATEGORIES_SUCCESSFULLY = "category.get_list_categories_successfully";
    public static final String CATEGORY_GET_CATEGORY_SUCCESSFULLY = "category.get_category_successfully";
    public static final String CATEGORY_UPDATE_SUCCESSFULLY = "category.update_category.update_successfully";
    public static final String CATEGORY_NOT_FOUND = "category.not_found";
    public static final String CANNOT_DELETE_CATEGORY_PRODUCT = "category.cannot_delete_category_product";
    public static final String CATEGORY_DELETE_SUCCESSFULLY = "category.delete_successfully";
    public static final String USER_NOT_FOUND = "user.not_found";
    public static final String USER_ACCOUNT_LOCKED = "user.account_locked";
    public static final String USER_FOUND_SUCCESS = "user.found_successfully";
    public static final String USER_UPDATE_PROFILE_SUCCESSFULLY = "user.update_profile_successfully";

    public static final String ORDER_INVALID_SHIPPING_DATE = "order.invalid_shipping_date";
    public static final String ORDER_NOT_FOUND = "order.not_found";
    public static final String ORDER_CANNOT_BE_UPDATED = "order.cannot_be_updated";
    public static final String ORDER_CREATED_SUCCESSFULLY = "order.created.successfully";
    public static final String ORDER_FETCHED_SUCCESSFULLY = "order.fetched_successfully";
    public static final String GET_ORDER_BY_ID_SUCCESSFULLY = "order.get_order_by_id_successfully";
    public static final String GET_ALL_ORDERS_SUCCESSFULLY = "order.get_all_order_success";
    public static final String ORDER_UPDATED_SUCCESSFULLY = "order.updated_successfully";
    public static final String ORDER_DELETE_SUCCESSFULLY = "order.delete_successfully";
    public static final String ORDER_RETRIEVED_SUCCESSFULLY = "order.retrieved_successfully";


    public static final String ORDERDETAIL_NOT_FOUND = "orderdetail.not_found";
    public static final String ORDERDETAIL_CREATED_SUCCESSFULLY = "orderdetail.created_successfully";
    public static final String ORDERDETAIL_GET_ALL_ORDERDETAILS_SUCCESS = "orderdetail.get_all_orderdetails_successfully";
    public static final String ORDERDETAIL_GET_BY_ID_SUCCESSFULLY = "orderdetail.get_orderdetail_by_id_successfully";
    public static final String ORDERDETAIL_FETCHED_SUCCESSFULLY = "orderdetail.fetched_successfully";
    public static final String ORDERDETAIL_UPDATE_SUCCESSFULLY = "orderdetail.updated_successfully";
    public static final String ORDERDETAIL_DELETE_SUCCESSFULLY = "orderdetail.delete_successfully";

    public static final String PRODUCT_NOT_FOUND = "product.not_found";
    public static final String PRODUCT_GET_BY_ID_SUCCESSFULLY = "product.get_product_by_id_successfully";
    public static final String PRODUCT_CREATED_SUCCESSFULLY = "product.created_successfully";
    public static final String PRODUCT_ERROR_MAX_5_IMAGES = "product.upload_images.error_max_5_images";
    public static final String PRODUCT_ERROR_FILE_LARGE = "product.upload_images.file_large";
    public static final String PRODUCT_ERROR_FILE_MUST_BE_IMAGE = "product.upload_images.file_must_be_image";
    public static final String PRODUCT_DELETE_SUCCESSFULLY = "product.delete_successfully";
    public static final String PRODUCT_INVALID_IMAGE_FORMAT = "product.invalid_image_format";
    public static final String PRODUCT_OUT_OF_STOCK = "product.out_of_stock";
    public static final String PRODUCTIMAGES_GET_BY_PRODUCT_ID_SUCCESSFULLY = "productimage.get_by_product_id_successfully";
    public static final String PRODUCT_ERROR_UPLOAD_FAIL ="product.error_upload_fail";
    public static final String PRODUCT_SUGGESTIONS_GET_SUCCESSFULLY = "product.get_suggestions_successfully";
    public static final String CART_ADD_PRODUCT_SUCCESSFULLY = "cart.add_product_successfully";
    public static final String CART_NOT_FOUND = "cart.not_found";
    public static final String CART_GET_CART_ITEMS_SUCCESSFULLY = "cart.get_cart_items_successfully";
    public static final String CART_DELETE_SUCCESSFULLY = "cart.delete_successfully";
    public static final String CARTDETAIL_GET_CARTDETAILS_BY_USER_ID_SUCCESSFULLY = "cartdetail.get_cartdetails_by_user_id_successfully";

    public static final String CARTDETAIL_NOT_FOUND = "cartdetail.not_found";

    public static final String CARTDETAIL_DELETE_SUCCESSFULLY = "cartdetail.delete_successfully";
    public static final String CARTDETAIL_UPDATE_SUCCESSFULLY = "cartdetail.update_successfully";

    public static final String CARTDETAIL_NOT_BELONG_TO_USER = "cartdetail.not_belong_to_user";

    public static final String PAYMENT_URL_GENERATED_SUCCESSFULLY = "payment.url_generated_successfully";

    public static final String ERROR_GENERATED_PAYMENT_URL = "payment.error_generating_payment_url";

    public static final String PAYMENT_QUERY_SUCCESSFULLY = "payment.query_successfully";

    public static final String PAYMENT_ERROR_QUERY_TRANSACTION = "payment.error_query_transaction";
    public static final String PAYMENT_REFUND_QUERY_TRANSACTION = "payment.refund_successfully";
    public static final String PAYMENT_REFUND_FAILED = "payment.refund_failed";
    public static final String PAYMENT_TRANSACTION_NOT_FOUND = "payment.transaction_not_found";
    public static final String PAYMENT_ORDER_NOT_FOUND = "payment.order_not_found";
    public static final String PAYMENT_UPDATE_SUCCESSFULLY = "payment.update_successfully";

    public static final String COMMENT_CREATED_SUCCESSFULLY = "comment.created_successfully";
    public static final String COMMENT_NOT_FOUND = "comment.not_found";
    public static final String COMMENT_GET_LIST_SUCCESSFULLY = "comment.get_list_successfully";

    public static final String ERROR_COMMENT_ALREADY_RATED = "error.comment.already_rated";

    public static final String ERROR_ONLY_PARENT_COMMENT_CAN_BE_RATED = "error.only_parent_comment_can_be_rated";
    public static final String RATING_SUBMIT_SUCCESSFULLY = "rating.submit_successfully";
    public static final String RATING_FETCH_SUCCESSFULLY = "rating.fetch_successfully";

    public static final String BRAND_CREATED_SUCCESSFULLY = "brand.create_successfully";
    public static final String BRAND_FETCHED_BY_CATEGORY_SUCCESSFULLY = "brand.fetched_by_category_successfully";
    public static final String BRAND_NOT_FOUND = "brand.not_found";

    public static final String VALUE_MIN_MAX_SUCCESSFULLY = "value.min.max.successfully";

    public static final String CREATED_ACCESS_TOKEN_SUCCESSFULLY = "access_token.created_successfully";
    public static final String REFRESH_TOKEN_NOT_FOUND = "refresh_token.not.found";

    public static final String REFRESH_TOKEN_REVOKED_SUCCESSFULLY = "refresh_token.revoked.successfully";
    public static final String REFRESH_TOKEN_REVOKED = "refresh_token.revoked";

    public static final String FAVORITE_ADD_PRODUCT_SUCCESSFULLY = "favorite.add_product_successfully";
    public static final String FAVORITE_REMOVE_PRODUCT_SUCCESSFULLY = "favorite.remove_product_successfully";

    public static final String FAVORITE_NOT_FOUND = "favorite.not_found";
    public static final String FAVORITE_GET_LIST_PRODUCTS_SUCCESSFULLY = "favorite.get_list_products_successfully";

    public static final String FAVORITE_GET_ITEMS_SUCCESSFULLY = "favorite.get_items_successfully";

    public static final String FAVORITE_REMOVE_ALL_PRODUCT_SUCCESSFULLY = "favorite.remove_all_product_successfully";

    public static final String COUPON_CODE_ALREADY_EXISTS = "coupon.code_already_exists";
    public static final String COUPON_CREATED_SUCCESSFULLY = "coupon.created_successfully";

    public static final String COUPON_NOT_FOUND = "coupon.not_found";
    public static final String COUPON_EXPIRED = "coupon.expired";
    public static final String COUPON_ALREADY_USED = "coupon.already_used";
    public static final String COUPON_APPLIED_SUCCESSFULLY = "coupon.applied_successfully";
    public static final String COUPON_INVALID = "coupon.invalid";


    public static final String USERCOUPON_NOT_FOUND = "usercoupon.not_found";

    public static final String SOCIAL_LOGIN_URL_GENERATED = "social.login_url_generated";

    public static final String PRODUCT_TOP_SELLER_FETCH_SUCCESSFULLY = "product.top_seller_fetch_successfully";
    public static final String PRODUCT_TOP_RATED_FETCH_SUCCESSFULLY = "product.top_rated_fetch_successfully";


    public static final String ACCESS_DENIED = "access.denied";

    public static final String STATISTICS_REVENUE_YEAR_SUCCESS = "statistics.revenue_year_successfully";
    public static final String STATISTICS_REVENUE_MONTH_SUCCESS = "statistics.revenue_month_successfully";

    public static final String STATISTICS_REVENUE_BY_YEAR_SUCCESS = "statistics.revenue_by_year_successfully";
    public static final String STATISTICS_AVAILABLE_ORDER_YEARS_SUCCESS = "statistics.available_order_years_successfully";

    public static final String STATISTICS_PENDING_ORDERS_COUNT_SUCCESS = "statistics.pending_orders_count_successfully";


}
