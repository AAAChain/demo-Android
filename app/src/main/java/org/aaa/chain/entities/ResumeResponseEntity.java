package org.aaa.chain.entities;

public class ResumeResponseEntity {

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

    public class ExtraEntity {
        private String desc;
        private String workours;
        private String company;
        private String birthday;
        private String latestWorkHours;
        private String name;
        private String latestCompany;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
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

        public String getLatestWorkHours() {
            return latestWorkHours;
        }

        public void setLatestWorkHours(String latestWorkHours) {
            this.latestWorkHours = latestWorkHours;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLatestCompany() {
            return latestCompany;
        }

        public void setLatestCompany(String latestCompany) {
            this.latestCompany = latestCompany;
        }
    }
}
