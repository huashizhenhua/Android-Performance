/*
 * Performance analysis
 * 
 * Test activity, executes all tests
 * 
 * Copyright (c) Thomas Gummerer 2011 | All rights reserved
 */

package com.tgummerer;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.widget.TextView;

public class Progress extends Activity {

    private final int iterations = 100;
    private String PREFS_NAME = "ALGORITHMS";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);
        
        // Empty database before adding new records
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from measurements");
        
        Algorithms alg = new Algorithms();
        alg.execute((Void[])null);
        db.close();
	}
    
    public void executeCAlgorithms() {
    	CAlgorithms calg = new CAlgorithms();
    	calg.execute((Void[])null);
    	
    }
    
    public void algorithmStarted(long langID, long algorithmID) {
    	TextView tv = (TextView)findViewById(R.id.progress_textview);
    	if (langID == 0)
    		tv.append("[Java] Algorithm " + algorithmID + " started...\n");
        else if (langID == 1)
    		tv.append("[C] Algorithm " + algorithmID + " started...\n");
        else if (langID == 2)
            tv.append("[Java] Memtest started...\n");
        else
            tv.append("[C] Memtest started...\n");
    }
    
    public void algorithmEnded(long langID, long algorithmID, long time) {
    	TextView tv = (TextView)findViewById(R.id.progress_textview);
    	DatabaseHelper dbhelper = new DatabaseHelper(this);
    	SQLiteDatabase db = dbhelper.getWritableDatabase();
    	
    	if (langID == 0)
    		tv.append("[Java] Algorithm " + algorithmID + " completed in " + time + " ns\n");
    	else
    		tv.append("[C] Algorithm " + algorithmID + " completed in " + time + " ns\n");
    	
    	ContentValues values = new ContentValues(3);
    	values.put("langid", langID);
    	values.put("algorithmid", algorithmID);
    	values.put("time", time);
    	db.insert("measurements", null, values);
    	db.close();
    }

    public void memAlgorithmEnded(long langID) {
        TextView tv = (TextView)findViewById(R.id.progress_textview);

        if (langID == 2)
            tv.append("[Java] Memtest completed\n");
        else
            tv.append("[C] Memtest completed\n");
    }
    
    private class Algorithms extends AsyncTask<Void, Long, Integer> {
    	
    	public Algorithms() {

    	}
    	
    	@Override
    	protected Integer doInBackground(Void... params) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            if (settings.getBoolean("algorithm1", true)) 
                forloop();
            if (settings.getBoolean("algorithm2", true)) 
                sort();
            if (settings.getBoolean("algorithm3", true)) 
                recursive();
            if (settings.getBoolean("algorithm4", true)) 
                classesTest();
            if (settings.getBoolean("memtest", true))
                memTest();
    		return 1;
    	}
    	
    	protected void onProgressUpdate(Long... progress) {
            if (progress[0] < 2) {
                if (progress[2] == 0)
                    algorithmStarted(progress[0], progress[1]);
                else
                    algorithmEnded(progress[0], progress[1], progress[2]);
            } else {
                if (progress[1] == 0)
                    algorithmStarted(progress[0], 0);
                else
                    memAlgorithmEnded(progress[0]);
            }
    	}

    	private void showProgress(long langID, long algorithmID, long time) {
    		Long[] array = new Long[3];
    		array[0] = langID;
    		array[1] = algorithmID;
    		array[2] = time;
    		publishProgress(array);
    	}
    	
    	
		protected void onPostExecute(Integer result) {
            // Execute the C algorithms after the Java algorithms
			executeCAlgorithms();
		}
    	 
    	// Algorithm 1, for loop doing nothing 
    	private void forloop () {
    		showProgress(0, 1, 0);
    		// Using System.nanoTime instead of System.getTimeInMillis is more accurate
    		// according to http://stackoverflow.com/questions/1770010/how-do-i-measure-time-elapsed-in-java
            long[] times = new long[iterations];
            for (int i = 0; i < iterations; i++) {
                long startTime = System.nanoTime();
                
                // ALGORITHM
                float k = 100000;
                for (int j = 0; j < 100000; j++)
                    k = k / j;
                // END ALGORITHM
                
                long endTime = System.nanoTime();
                times[i] = endTime - startTime;
            }
            float average = 0;
            for (int i = 0; i < iterations; i++) 
                average += times[i] / iterations;

    		showProgress(0, 1, (long)average);
    	}

        // Algorithm 2, Random quicksort of a backwards sorted array
    	// If it wouldn't be random there is a high possibility of a stackoverflow
        private void sort() {
        	showProgress(0, 2, 0);
            long[] times = new long[iterations];
            for (int j = 0; j < iterations; j++) {
                long startTime = System.nanoTime();
                // ALGORITHM
                int[] arr = new int[500];
                for (int i = 0; i < arr.length; i++)
                    arr[i] = arr.length - i;
                
                quicksort(arr, 0, arr.length - 1);
                // END ALGORITHM
                
                long endTime = System.nanoTime();
                times[j] = endTime - startTime;
            }
            float average = 0;
            for (int i = 0; i < iterations; i++) 
                average += times[i] / iterations;

    		showProgress(0, 2, (long)average);
        }

        private void quicksort(int arr[], int start, int end) {
            int i = start;
            int k = end;
            Random r = new Random();

            if (end - start >= 1) {
                int p = r.nextInt(end - start);
                p = p + start;
                int pivot = arr[p];
                while (k > i) {
                    while (arr[i] <= pivot && i <= end && k > i)
                        i++;
                    while (arr[k] > pivot && k >= start && k >= i)
                        k--;
                    if (k > i)
                        swap(arr, i, k);
                }
                swap(arr, start, k);

                quicksort(arr, start, k - 1);
                quicksort(arr, k + 1, end);
            } else 
                return;
        }

        private void swap(int arr[], int i, int k) {
            int t = arr[i];
            arr[i] = arr[k];
            arr[k] = t;
        }	

        // Algorithm 3, stupid recursive function
        // Android chokes on recursions deeper then 325 (or slightly deeper
        // but 350 didn't work), hence the small number.
        private void recursive() {
            showProgress(0, 3, 0);
            long[] times = new long[iterations];
            for (int j = 0; j < iterations; j++) {
                long startTime = System.nanoTime();

                recursive(19);

                long endTime = System.nanoTime();
                times[j] = endTime - startTime;
            }
            float average = 0;
            for (int i = 0; i < iterations; i++) 
                average += times[i] / iterations;

    		showProgress(0, 3, (long)average);
        }

        private long recursive(long n) {
            if (n < 2)
                return 1;
            return recursive(n - 1) + recursive(n - 2);
        }

        // Algorithm 4, test from the web
        // Source: http://blog.dhananjaynene.com/2008/07/performance-comparison-c-java-python-ruby-jython-jruby-groovy/
        private void classesTest() {
            showProgress(0, 4, 0);
            long[] times = new long[iterations];
            for (int j = 0; j < iterations; j++) {
                long startTime = System.nanoTime();

                Chain chain = new Chain(500);
                chain.kill(250);

                long endTime = System.nanoTime();
                times[j] = endTime - startTime;
            }
            float average = 0;
            for (int i = 0; i < iterations; i++)
                average += times[i] / iterations;

            showProgress(0, 4, (long)average);
        }

        private void memTest() {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
            showProgress(2, 0, 0);

            DatabaseHelper dbHelper = new DatabaseHelper(Progress.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("delete from memtests;");
            db.close();

            final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            final int[] pid = {android.os.Process.myPid()};
            
            executor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    DatabaseHelper dbHelper = new DatabaseHelper(Progress.this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    MemoryInfo[] info = activityManager.getProcessMemoryInfo(pid);

                    ContentValues values = new ContentValues(2);
                    values.put("type", 0);
                    values.put("memusage", info[0].dalvikPrivateDirty);
                    db.insert("memtests", null, values);
                    db.close();
                }
            }, 0, 100, TimeUnit.MILLISECONDS);

            Chain chain = new Chain(50000);
            for (int i = 0; i < 50000 / 6; i++)
                chain.kill(i * 5);
            chain = null;

            Chain chain2 = new Chain(50000);
            for (int i = 0; i < 50000; i++)
                chain2.kill(0);

            chain2 = null;

            executor.shutdownNow();
            showProgress(2, 1, 0);
        }
    }

    private class CAlgorithms extends AsyncTask<Void, Long, Void> {
    	
    	@Override
    	protected Void doInBackground(Void... params) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            if (settings.getBoolean("algorithm1", true)) 
                forloop();
            if (settings.getBoolean("algorithm2", true)) 
                sort();
            if (settings.getBoolean("algorithm3", true)) 
                recursive();
            if (settings.getBoolean("algorithm4", true)) 
                classesTest();
            if (settings.getBoolean("memtest", true))
                memTest();
    		return null;
    	}

    	protected void onProgressUpdate(Long... progress) {
            if (progress[0] < 2) {
                if (progress[2] == 0)
                    algorithmStarted(progress[0], progress[1]);
                else
                    algorithmEnded(progress[0], progress[1], progress[2]);
            } else {
                if (progress[1] == 0)
                    algorithmStarted(progress[0], 0);
                else
                    memAlgorithmEnded(progress[0]);
            }
    	}
    	
    	private void showProgress(long langID, long algorithmID, long time) {
    		Long[] array = new Long[3];
    		array[0] = langID;
    		array[1] = algorithmID;
    		array[2] = time;
    		publishProgress(array);
    	}
    	
    	// Algorithm 1, for loop doing nothing 
    	private void forloop () {
    		showProgress(1, 1, 0);
    		// Using System.nanoTime instead of System.getTimeInMillis is more accurate
    		// according to http://stackoverflow.com/questions/1770010/how-do-i-measure-time-elapsed-in-java
            long[] times = new long[iterations];
            for (int j = 0; j < iterations; j++) {
                long startTime = System.nanoTime();

                cforloop();

                long endTime = System.nanoTime();
                times[j] = endTime - startTime;
            }
            float average = 0;
            for (int i = 0; i < iterations; i++) 
                average += times[i] / iterations;

    		showProgress(1, 1, (long)average);
    	}

        // Algorithm 2, Random quicksort of a backwards sorted array of size 10000
    	// If it wouldn't be random there is a high possibility of a stackoverflow
        private void sort() {
        	showProgress(1, 2, 0);
            long[] times = new long[iterations];
            for (int j = 0; j < iterations; j++) {
                long startTime = System.nanoTime();
                csort();

                long endTime = System.nanoTime();
                times[j] = endTime - startTime;
            }
            float average = 0;
            for (int i = 0; i < iterations; i++) 
                average += times[i] / iterations;

            showProgress(1, 2, (long)average);
        }  

        // Algorithm 3
        private void recursive() {
            showProgress(1, 3, 0);
            long[] times = new long[iterations];
            for (int j = 0; j < iterations; j++) {
                long startTime = System.nanoTime();

                crecursive();

                long endTime = System.nanoTime();
                times[j] = endTime - startTime;
            }
            float average = 0;
            for (int i = 0; i < iterations; i++) 
                average += times[i] / iterations;

    		showProgress(1, 3, (long)average);
        }
            
        // Algorithm 4, test from the web
        // Source: http://blog.dhananjaynene.com/2008/07/performance-comparison-c-java-python-ruby-jython-jruby-groovy/
        private void classesTest() {
            showProgress(1, 4, 0);
            long[] times = new long[iterations];
            for (int j = 0; j < iterations; j++) {
                long startTime = System.nanoTime();

                cclassestest();

                long endTime = System.nanoTime();
                times[j] = endTime - startTime;
            }
            float average = 0;
            for (int i = 0; i < iterations; i++)
                average += times[i] / iterations;

            showProgress(1, 4, (long)average);
        }

        private void memTest() {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
            showProgress(3, 0, 0);

            final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            final int[] pid = {android.os.Process.myPid()};
            
            executor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    DatabaseHelper dbHelper = new DatabaseHelper(Progress.this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    MemoryInfo[] info = activityManager.getProcessMemoryInfo(pid);

                    ContentValues values = new ContentValues(2);
                    values.put("type", 1);
                    values.put("memusage", info[0].nativePrivateDirty);
                    db.insert("memtests", null, values);
                    db.close();
                }
            }, 0, 100, TimeUnit.MILLISECONDS);

            cmemtest();

            executor.shutdownNow();
            showProgress(3, 1, 0);
        }

    }
    
    public native float cforloop();
	public native void csort();
    public native long crecursive();
    public native void cclassestest();
    public native void cmemtest();
    
    static {
        System.loadLibrary("algorithms");
    }
}	
