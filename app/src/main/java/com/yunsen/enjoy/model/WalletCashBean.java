package com.yunsen.enjoy.model;

/**
 * Created by Administrator on 2018/5/31.
 * 提现明细
 */

public class WalletCashBean {
    /**
     * id : 3033
     * user_id : 13869
     * user_name : 17688929088
     * withdraw_price : 10
     * paypassword : 12345678
     * last_balance : 0
     * bank_id : 3033
     * bank_card : 6214837842003182
     * bank_account : 农业银行
     * bank_name : 农业银行
     * bank_branch : null
     * pay_time : 2018-07-30 17:53:01
     * status : 1
     * content :
     * add_time : 2018-07-30 17:53:01
     * update_time : 2018-07-30 17:58:49
     */

    private int id;
    private int user_id;
    private String user_name;
    private double withdraw_price;
    private String paypassword;
    private double last_balance;
    private int bank_id;
    private String bank_card;
    private String bank_account;
    private String bank_name;
    private Object bank_branch;
    private String pay_time;
    private int status;
    private String content;
    private String add_time;
    private String update_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public double getWithdraw_price() {
        return withdraw_price;
    }

    public void setWithdraw_price(double withdraw_price) {
        this.withdraw_price = withdraw_price;
    }

    public String getPaypassword() {
        return paypassword;
    }

    public void setPaypassword(String paypassword) {
        this.paypassword = paypassword;
    }

    public double getLast_balance() {
        return last_balance;
    }

    public void setLast_balance(double last_balance) {
        this.last_balance = last_balance;
    }

    public int getBank_id() {
        return bank_id;
    }

    public void setBank_id(int bank_id) {
        this.bank_id = bank_id;
    }

    public String getBank_card() {
        return bank_card;
    }

    public void setBank_card(String bank_card) {
        this.bank_card = bank_card;
    }

    public String getBank_account() {
        return bank_account;
    }

    public void setBank_account(String bank_account) {
        this.bank_account = bank_account;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public Object getBank_branch() {
        return bank_branch;
    }

    public void setBank_branch(Object bank_branch) {
        this.bank_branch = bank_branch;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }


}