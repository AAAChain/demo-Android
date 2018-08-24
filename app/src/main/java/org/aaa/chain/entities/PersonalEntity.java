package org.aaa.chain.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class PersonalEntity implements Parcelable {

    private int sex;
    private String workHours;
    private String birthday;
    private String company;

    public static final Creator<PersonalEntity> CREATOR = new Creator<PersonalEntity>() {
        @Override public PersonalEntity createFromParcel(Parcel in) {
            PersonalEntity personalEntity = new PersonalEntity();
            personalEntity.setSex(in.readInt());
            personalEntity.setWorkHours(in.readString());
            personalEntity.setBirthday(in.readString());
            personalEntity.setCompany(in.readString());
            return personalEntity;
        }

        @Override public PersonalEntity[] newArray(int size) {
            return new PersonalEntity[size];
        }
    };

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getWorkHours() {
        return workHours;
    }

    public void setWorkHours(String workHours) {
        this.workHours = workHours;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sex);
        dest.writeString(workHours);
        dest.writeString(birthday);
        dest.writeString(company);
    }
}
