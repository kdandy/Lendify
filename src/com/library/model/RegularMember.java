package com.library.model;

public class RegularMember extends Member {
    private String occupation;
    private String employerName;
    private boolean isPremium;
    
    // Constructors
    public RegularMember() {
        super();
    }
    
    public RegularMember(Member member, String occupation) {
        super();
        // Copy member properties
        setMemberProperties(member);
        this.occupation = occupation;
        this.isPremium = false;
    }
    
    public RegularMember(Member member, String occupation, String employerName, boolean isPremium) {
        super();
        // Copy member properties
        setMemberProperties(member);
        this.occupation = occupation;
        this.employerName = employerName;
        this.isPremium = isPremium;
    }
    
    private void setMemberProperties(Member member) {
        // Copy Person properties
        super.setName(member.getName());
        super.setAddress(member.getAddress());
        super.setPhoneNumber(member.getPhoneNumber());
        super.setEmail(member.getEmail());
        
        // Since we can't directly access private fields of Member, 
        // we need a workaround in a real implementation.
        // Ideally, Member would have a method to copy its state to another Member instance.
    }
    
    // Getters and Setters
    public String getOccupation() {
        return occupation;
    }
    
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    
    public String getEmployerName() {
        return employerName;
    }
    
    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }
    
    public boolean isPremium() {
        return isPremium;
    }
    
    public void setPremium(boolean premium) {
        isPremium = premium;
    }
    
    @Override
    public int getMaxBooks() {
        return isPremium ? 10 : 5; // Premium members can borrow more books
    }
    
    @Override
    public int getMaxLoanDays() {
        return isPremium ? 30 : 14; // Premium members can borrow for a longer period
    }
    
    @Override
    public String toString() {
        return "RegularMember{" +
                "memberId='" + getMemberId() + '\'' +
                ", name='" + getName() + '\'' +
                ", occupation='" + occupation + '\'' +
                ", employerName='" + employerName + '\'' +
                ", isPremium=" + isPremium +
                ", status=" + getStatus() +
                ", active=" + isActive() +
                '}';
    }
}