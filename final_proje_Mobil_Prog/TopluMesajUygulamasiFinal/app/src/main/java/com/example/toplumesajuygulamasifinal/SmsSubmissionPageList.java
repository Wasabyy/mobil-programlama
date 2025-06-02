package com.example.toplumesajuygulamasifinal;

public class SmsSubmissionPageList {
    private String name;
    private String number;
    private String group;

    public SmsSubmissionPageList(){

    }
    public SmsSubmissionPageList(String name){this.name=name;}
    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SmsSubmissionPageList that = (SmsSubmissionPageList) obj;
        return number != null ? number.equals(that.number) : that.number == null;
    }

    @Override
    public int hashCode() {
        return number != null ? number.hashCode() : 0;
    }
}
