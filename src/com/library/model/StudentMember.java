package com.library.model;

public class StudentMember extends Member {
    private String studentId;
    private String faculty;
    private String department;
    private int yearOfStudy;
    
    // Constructors
    public StudentMember() {
        super();
    }
    
    public StudentMember(Member member, String studentId, String faculty) {
        super();
        // Copy member properties
        setMemberProperties(member);
        this.studentId = studentId;
        this.faculty = faculty;
    }
    
    public StudentMember(Member member, String studentId, String faculty, String department, int yearOfStudy) {
        super();
        // Copy member properties
        setMemberProperties(member);
        this.studentId = studentId;
        this.faculty = faculty;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
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
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getFaculty() {
        return faculty;
    }
    
    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public int getYearOfStudy() {
        return yearOfStudy;
    }
    
    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }
    
    @Override
    public int getMaxBooks() {
        return 8; // Students can borrow more books
    }
    
    @Override
    public int getMaxLoanDays() {
        return 21; // Students can borrow for a longer period
    }
    
    @Override
    public String toString() {
        return "StudentMember{" +
                "memberId='" + getMemberId() + '\'' +
                ", name='" + getName() + '\'' +
                ", studentId='" + studentId + '\'' +
                ", faculty='" + faculty + '\'' +
                ", department='" + department + '\'' +
                ", yearOfStudy=" + yearOfStudy +
                ", status=" + getStatus() +
                ", active=" + isActive() +
                '}';
    }
}