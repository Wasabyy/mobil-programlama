package com.example.toplumesajuygulamasifinal;

import java.io.Serializable;

public class TelephoneDirectoryList implements Serializable {
    private String userNumber;
    private String userName;
    private String userFoto;
    private boolean isSelected = false;
    private String group;

    public TelephoneDirectoryList(){

    }

    public TelephoneDirectoryList(String userNumber, String userName, String userFoto, boolean isSelected){
        this.userNumber = userNumber;
        this.userName = userName;
        this.userFoto = userFoto;
        this.isSelected = isSelected;
    }

    public boolean isSelected(){return isSelected;}
    public void setSelected(boolean selected){isSelected=selected;}
    public String getUserNumber(){return userNumber;}
    public void setUserNumber(String userNumber){this.userNumber=userNumber;}
    public String getUserName(){return userName;}
    public void setUserName(String userName){this.userName=userName;}
    public String getUserFoto(){return userFoto;}
    public void setUserFoto(String userFoto){this.userFoto=userFoto;}
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
}
