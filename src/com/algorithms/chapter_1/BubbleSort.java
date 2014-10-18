package com.algorithms.chapter_1;
/**
 * 冒泡排序
 */
public class BubbleSort {
	
	static int[] a = {4,5,7,1,8,3,13,42,21,64,32,34,65,87,43,96,5,8};
	
	public static void main(String args[]){
		for(int j=a.length-1;j>=0;j--){
			for(int i=0;i<j;i++){
				if(a[i]<a[i+1]){
					int temp = a[i];
					a[i] = a[i+1];
					a[i+1] = temp;
				}
			}
		}
		for(int i=0;i<a.length;i++){
			System.out.println(a[i]);
		}
	}
}
