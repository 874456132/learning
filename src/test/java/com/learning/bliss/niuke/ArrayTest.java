package com.learning.bliss.niuke;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/3/24 16:49
 * @Version 1.0
 */
public class ArrayTest {


    public static void main(String[] args) {
        //合并两个有序数组成一个有序数组
        //merge();

        //学生最高成绩
        //getStudentHighestGrade();

        //明明的随机数 数组去重并排序
        //arraysDeduplicatedSorted();
    }

    /**
     * 合并两个有序数组成一个有序数组
     */
    private static void merge() {
        int a[] = {1, 4, 7, 11, 15, 15, 18, 30};
        int b[] = {1, 2, 3, 6, 9, 10, 11, 31};
        int result[] = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;
        //按位循环比较两个数组，较小元素的放入新数组
        // 下标加一（注意，较大元素对应的下标不加一），直到某一个下标等于数组长度时退出循环
        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) {
                result[k++] = a[i++];
            } else {
                result[k++] = b[j++];
            }
        }
        /* 后面连个while循环是用来保证两个数组比较完之后剩下的一个数组里的元素能顺利传入 *
         * 此时较短数组已经全部放入新数组，较长数组还有部分剩余，最后将剩下的部分元素放入新数组，大功告成*/
        while (i < a.length)
            result[k++] = a[i++];
        while (j < b.length)
            result[k++] = b[j++];

        System.out.println(Arrays.toString(result));
    }

    //https://www.zhihu.com/question/524696493/answer/2479588268
    //学生最高成绩
    private static void getStudentHighestGrade() {
        Scanner scanner = new Scanner(System.in);
        int studentsnNum = Integer.parseInt(scanner.next());//学生数
        int options = Integer.parseInt(scanner.nextLine().trim());//操作数
         //成绩数组
        String grades[] = scanner.nextLine().split(" ");
        StringBuilder result = new StringBuilder();
        while (options > 0){
            String work = scanner.nextLine();
            String questionOrUpdate[] = work.split(" ");

            if("Q".equals(questionOrUpdate[0])) {
                int start = Integer.parseInt(questionOrUpdate[1]);
                int end = Integer.parseInt(questionOrUpdate[2]);
                int grade = Integer.parseInt(grades[start - 1]);
                Arrays.sort(grades);
                while (start <= end) {
                    int a = Integer.parseInt(grades[start++ - 1]);
                    if(grade < a){
                        grade = a;
                    }
                }
                result = result.append(grade).append(" ") ;
            } else if("U".equals(questionOrUpdate[0])) {
                grades[Integer.parseInt(questionOrUpdate[1]) - 1] = questionOrUpdate[2];
            }
            options--;
        }
        String[] results = result.toString().split(" ");
        for (int i = 0; i < results.length; i++)
            System.out.println(results[i]);

    }

    //明明的随机数 数组去重并排序
    private static void arraysDeduplicatedSorted() {
        Scanner scanner = new Scanner(System.in);

        int nums = scanner.nextInt();
        int[] arr = new int[nums];
        int i = 0;
        arr[i] = scanner.nextInt();
        while (nums-- > 1) {
            int a = scanner.nextInt();
            if (a > arr[i]) {
                arr[++i] = a;
            } else if (a < arr[0]) {
                int l = i;
                while (l >= 0) {
                    arr[l + 1] = arr[l];
                    l--;
                }
                arr[0] = a;
                ++i;
            } else {
                int max = i;
                int min = 0;
                int middle = min + ((max - min) >> 2);
                while(min <= max){
                    if(a == arr[middle] || a == arr[middle + 1]){
                        middle = -1;
                        break;
                    }
                    if(arr[middle] < a && a < arr[middle + 1]) {
                        break;
                    }
                    if(a < arr[middle]){
                        max = middle - 1;
                        middle = min + ((max - min) >> 2);
                    }
                    if(a > arr[middle + 1]){
                        min = middle + 2;
                        middle = min + ((max - min) >> 2);
                    }
                }
                if (middle == -1) {
                    continue;
                }
                int l = i;
                int s = middle;
                while ((l - s) > 0) {
                    arr[l + 1] = arr[l];
                    l--;
                }
                arr[middle + 1] = a;
                ++i;
            }
        }
        int ii = 0;
        while (ii <= i) {
            System.out.println(arr[ii++]);
        }
    }
}
