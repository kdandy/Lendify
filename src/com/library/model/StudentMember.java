package com.library.model;

public class StudentMember extends Member {
    private String studentId;
    private String faculty;
    private String department;
    private int yearOfStudy;
    
    // konsruktor
    public StudentMember() {
        super();
    }
    
    public StudentMember(Member member, String studentId, String faculty) {
        super();
        setMemberProperties(member);
        this.studentId = studentId;
        this.faculty = faculty;
    }
    
    public StudentMember(Member member, String studentId, String faculty, String department, int yearOfStudy) {
        super();
        setMemberProperties(member);
        this.studentId = studentId;
        this.faculty = faculty;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
    }
    
    private void setMemberProperties(Member member) {
        super.setName(member.getName());
        super.setAddress(member.getAddress());
        super.setPhoneNumber(member.getPhoneNumber());
        super.setEmail(member.getEmail());
    }
    
    // getter dan setter
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
        return 8;
    }
    
    @Override
    public int getMaxLoanDays() {
        return 21;
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