package org.aaa.chain.entities;

import java.util.List;

public class OrderResponseEntity {

    private boolean success;
    private String message;
    private String errorcode;
    private List<OrderDataEntity> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public List<OrderDataEntity> getData() {
        return data;
    }

    public void setData(List<OrderDataEntity> data) {
        this.data = data;
    }

    public class OrderDataEntity {
        private long id;
        private String buyer;
        private String seller;
        private String goodId;
        private int price;
        private long createTime;
        private long payTime;
        private String payRefId;
        private String deliverRefId;
        private int status;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getBuyer() {
            return buyer;
        }

        public void setBuyer(String buyer) {
            this.buyer = buyer;
        }

        public String getSeller() {
            return seller;
        }

        public void setSeller(String seller) {
            this.seller = seller;
        }

        public String getGoodId() {
            return goodId;
        }

        public void setGoodId(String goodId) {
            this.goodId = goodId;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getPayTime() {
            return payTime;
        }

        public void setPayTime(long payTime) {
            this.payTime = payTime;
        }

        public String getPayRefId() {
            return payRefId;
        }

        public void setPayRefId(String payRefId) {
            this.payRefId = payRefId;
        }

        public String getDeliverRefId() {
            return deliverRefId;
        }

        public void setDeliverRefId(String deliverRefId) {
            this.deliverRefId = deliverRefId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
