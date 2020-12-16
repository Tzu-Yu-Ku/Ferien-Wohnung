package fewodre.catalog.holidayhomes;

import org.javamoney.moneta.Money;
import fewodre.utils.Place;

public class HolidayHomeForm {
    private String name;
    private String description;
    private String hostUuid;
    private String street;
    private String number;
    private String postalnumber;
    private String city;
    private int capacity;
    private int price;

    public HolidayHomeForm(){
        this.name = name;
        this.description = description;
        this.hostUuid = hostUuid;
        this.street = street;
        this.number = number;
        this.postalnumber = postalnumber;
        this.city = city;
        this.capacity = capacity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPostalnumber() {
        return postalnumber;
    }

    public void setPostalnumber(String postalnumber) {
        this.postalnumber = postalnumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public HolidayHome toNewHolidayHome(String hostMail) {
        return new HolidayHome(getName(),hostMail,getDescription(),"house2.png",new Place(getStreet(), getNumber(), getPostalnumber(), getCity(), 1, 1), true,getCapacity(),Money.of(getPrice(), "EUR"));
	}

}