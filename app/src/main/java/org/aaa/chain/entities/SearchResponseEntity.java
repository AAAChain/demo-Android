package org.aaa.chain.entities;

import java.util.List;

public class SearchResponseEntity {

    private int page;
    private int pages;
    private List<DocsResponse> docs;
    private int total;
    private int limit;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<DocsResponse> getDocs() {
        return docs;
    }

    public void setDocs(List<DocsResponse> docs) {
        this.docs = docs;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public class DocsResponse {
        private String _id;
        private String hashId;
        private String account;
        private ExtraEntity extra;
        private int __v;
        private long size;
        private String publicKey;
        private String timestamp;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getHashId() {
            return hashId;
        }

        public void setHashId(String hashId) {
            this.hashId = hashId;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public ExtraEntity getExtra() {
            return extra;
        }

        public void setExtra(ExtraEntity extra) {
            this.extra = extra;
        }

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }

    public class ExtraEntity {

        private String latestWorkHours;
        private String workours;
        private String company;
        private String birthday;
        private String name;
        private String desc;
        private String price;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getLatestWorkHours() {
            return latestWorkHours;
        }

        public void setLatestWorkHours(String latestWorkHours) {
            this.latestWorkHours = latestWorkHours;
        }

        public String getWorkours() {
            return workours;
        }

        public void setWorkours(String workours) {
            this.workours = workours;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
