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
        private String startTime;
        private String company;
        private String birthday;
        private String lastWorkingHour;
        private String name;
        private String lastCompany;
        private String sex;
        private String jobContentInfo;

        public String getJobContentInfo() {
            return jobContentInfo;
        }

        public void setJobContentInfo(String jobContentInfo) {
            this.jobContentInfo = jobContentInfo;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
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

        public String getLastWorkingHour() {
            return lastWorkingHour;
        }

        public void setLastWorkingHour(String lastWorkingHour) {
            this.lastWorkingHour = lastWorkingHour;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastCompany() {
            return lastCompany;
        }

        public void setLastCompany(String lastCompany) {
            this.lastCompany = lastCompany;
        }
    }
}
