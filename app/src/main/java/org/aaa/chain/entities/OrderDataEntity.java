package org.aaa.chain.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderDataEntity implements Parcelable {
    private long id;
    private String buyer;
    private String seller;
    private String goodId;
    private int price;
    private long createTime;
    private long payTime;
    private String payMsg;
    private String deliverMsg;
    private int status;
    private String ext1;

    public OrderDataEntity() {
    }

    protected OrderDataEntity(Parcel in) {
        id = in.readLong();
        buyer = in.readString();
        seller = in.readString();
        goodId = in.readString();
        price = in.readInt();
        createTime = in.readLong();
        payTime = in.readLong();
        payMsg = in.readString();
        deliverMsg = in.readString();
        status = in.readInt();
        ext1 = in.readString();
    }

    public static final Creator<OrderDataEntity> CREATOR = new Creator<OrderDataEntity>() {
        @Override public OrderDataEntity createFromParcel(Parcel in) {
            return new OrderDataEntity(in);
        }

        @Override public OrderDataEntity[] newArray(int size) {
            return new OrderDataEntity[size];
        }
    };

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

    public String getPayMsg() {
        return payMsg;
    }

    public void setPayMsg(String payMsg) {
        this.payMsg = payMsg;
    }

    public String getDeliverMsg() {
        return deliverMsg;
    }

    public void setDeliverMsg(String deliverMsg) {
        this.deliverMsg = deliverMsg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(buyer);
        dest.writeString(seller);
        dest.writeString(goodId);
        dest.writeInt(price);
        dest.writeLong(createTime);
        dest.writeLong(payTime);
        dest.writeString(payMsg);
        dest.writeString(deliverMsg);
        dest.writeInt(status);
        dest.writeString(ext1);
    }
}
