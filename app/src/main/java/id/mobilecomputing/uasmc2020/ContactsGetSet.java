package id.mobilecomputing.uasmc2020;

public class ContactsGetSet {
    private String ContactImage;
    private String ContactName;
    private String ContactNumber;

    public String getContactImage(){
        return ContactImage;
    }

    public String getContactName(){
        return ContactName;
    }

    public String getContactNumber(){
        return ContactNumber;
    }

    public void setContactImage(String contactImage){
        ContactImage = contactImage;
    }

    public void setContactName(String contactName){
        ContactName = contactName;
    }

    public void setContactNumber(String contactNumber){
        ContactNumber = contactNumber;
    }
}
