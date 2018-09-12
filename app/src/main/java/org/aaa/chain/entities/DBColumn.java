package org.aaa.chain.entities;

public interface DBColumn {

    class Response {
        public static String RESPONSE_TABLE_NAME = "resume_response_info";
        public static String _ID = "_id";
        public static String ACCOUNT = "account";
        public static String HASH_ID = "hashId";
        public static String EXTRA = "extra";
        public static String __V = "__v";
        public static String SIZE = "size";
        public static String PUBLIC_KEY = "publicKey";
        public static String TIMESTAMP = "timestamp";
    }

    class Request {

        public static String REQUEST_TABLE_NAME = "resume_request_info";
        public static String _ID = "_id";
        public static String FILE_NAME = "filename";
        public static String ACCOUNT = "account";
        public static String FILE_PATH = "filepath";
        public static String METADATA = "metadata";
        public static String OPTIONS = "options";
        public static String SIGNATURE = "signature";
    }
}
