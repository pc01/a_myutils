package com.algorithms.chapter_1;

/**
 * 排序1   简化桶排序
 * 时间复杂度为   M b的长度   N a的长度
 * O(M+N+M+N)=O(2*(M+N))=O(M+N)
 *
 */
public class SimpleSort {
	
	static int[] a = {4,5,7,1,8,3,13,42,21,64,32,34,65,87,43,96,5,8};
	
	public static void main(String args[]){
		int[] b = new int[100];
		for(int i = 0;i<100;i++){
			b[i]=0;
		}
		for(int i = 0;i<a.length; i++){
			b[a[i]]++;
		}
		for(int i = 0;i<100;i++){
			for(int j = 0;j<b[i]; j++){
				System.out.println(i);
			}
		}
	}
}
