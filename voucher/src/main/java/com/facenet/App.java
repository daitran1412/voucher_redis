package com.facenet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import redis.clients.jedis.Jedis;

/**
 * redis example
 * @author Tran Dinh Dai
 * @version 1.0
 * @since 2023-3-14 2:07:00
 *
 */
public class App {

    static Jedis jedis = new Jedis("localhost", 6379);
    
    public static void main(String[] args) {

        // voucher1 id 11JI company facenet expiry_time 3000
        Map<String, String> voucher1 = new HashMap<String, String>();
        voucher1.put("id", "11JI");
        voucher1.put("company", "facenet");
        voucher1.put("expiry_time", "3000");
        jedis.hmset("voucher1", voucher1);

        // voucher2 id 12JI company facenet expiry_time 6000
        Map<String, String> voucher2 = new HashMap<String, String>();
        voucher2.put("id", "12JI");
        voucher2.put("company", "facenet");
        voucher2.put("expiry_time", "6000");
        jedis.hmset("voucher2", voucher2);

        // voucher3 id 13JI company facenet expiry_time -1200
        Map<String, String> voucher3 = new HashMap<String, String>();
        voucher3.put("id", "13JI");
        voucher3.put("company", "facenet");
        voucher3.put("expiry_time", "-1200");
        jedis.hmset("voucher3", voucher3);

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Enter voucher: ");
            String voucher = sc.nextLine();
            if (voucher.equals("exit")) {
                break;
            }
            if (voucher.equals("list")) {
                System.out.println("List of vouchers: " + jedis.keys("*"));
                continue;
            }
            if (voucher.equals("add")) {
                System.out.println("Enter voucher name: ");
                String voucherName = sc.nextLine();
                System.out.println("Enter id: ");
                String id = sc.nextLine();
                System.out.println("Enter company: ");
                String company = sc.nextLine();
                System.out.println("Enter expiry time: ");
                int expiry = sc.nextInt();
                sc.nextLine();
                addVoucher(voucherName, id, company, expiry);
                continue;
            }
            if (checkVoucher(voucher)) {
                System.out.println("Do you want to use this voucher? (y/n)");
                String answer = sc.nextLine();
                if (answer.toLowerCase().equals("y")) {
                    deleteVoucher(voucher);
                    System.out.println("Voucher has been used.");
                }
            }
        }
        sc.close();
        
    }

    /** 
     * @param voucher
     * @return boolean
     * @description check voucher and print voucher info
     */
    public static boolean checkVoucher (String voucher)
    {

        if(jedis.exists(voucher)) {
            Map<String, String> voucherInfo = jedis.hgetAll(voucher);
            // check expiry time
            int expiry = Integer.parseInt(voucherInfo.get("expiry_time"));
            if (expiry < 0) {
                System.out.println("This voucher has expired.");
                return false;
            } else {
                System.out.println("ID: " + voucherInfo.get("id"));
                System.out.println("Company: " + voucherInfo.get("company"));
                System.out.println("Expiry: " + expiry + "s");
                return true;
            }
        } else {
            System.out.println("This voucher does not exist.");
            return false;
        }

    }

    /** 
     * @param voucher
     * @return void
     * @description delete voucher
     */
    public static void deleteVoucher (String voucher)
    {
        jedis.del(voucher);
    }

    public static void addVoucher (String voucherName, String id, String company, int expiry)
    {

        Map<String, String> voucher = new HashMap<String, String>();
        voucher.put("id", id);
        voucher.put("company", company);
        voucher.put("expiry_time", String.valueOf(expiry));
        jedis.hmset(voucherName, voucher);
        
    }

}


