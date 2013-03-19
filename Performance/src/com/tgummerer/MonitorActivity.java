/*
 * Performance analysis
 * 
 * Monitor apps running on the phone
 * 
 * Copyright (c) Thomas Gummerer 2011 | All rights reserved
 */

package com.tgummerer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorActivity extends Activity {

    private ExpandableListAdapter taskMonitorAdapter;

    private ActivityManager activityManager;
    private Object[] taskinfo;
    private int totalMem;
    
    private static int servicetype = 0;
    private static int servicepid = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);
        
        activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        taskinfo = activityManager.getRunningAppProcesses().toArray();

        taskMonitorAdapter = new MonitorAdapter();
        ExpandableListView listView = (ExpandableListView)findViewById(R.id.monitorView);
        listView.setAdapter(taskMonitorAdapter); 

        FileInputStream fstream;
        try {
            fstream = new FileInputStream("/proc/meminfo");

            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String totalText = br.readLine();
            String[] tmp = totalText.split(" ");
            totalMem = Integer.valueOf(tmp[tmp.length - 2]);
            // Exceptions that shouldn't happen
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    public class MonitorAdapter extends BaseExpandableListAdapter {

    	@Override
		public ActivityManager.RunningAppProcessInfo getChild(int categoryPosition, int childPosition) {
            return (RunningAppProcessInfo)taskinfo[categoryPosition];
        }

    	@Override
    	public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

    	@Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
	            LayoutInflater inflater = (LayoutInflater) MonitorActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = inflater.inflate(R.layout.monitorchild, null);
	        }
	        TextView textView = (TextView) convertView.findViewById(R.id.appimportance);
	        textView.setText(getImportance(getGroup(groupPosition).importance));
	        
	        final int[] pid = {getGroup(groupPosition).pid};
	        MemoryInfo[] info = activityManager.getProcessMemoryInfo(pid);

	        TextView memView = (TextView) convertView.findViewById(R.id.dalvikpss);
	        memView.setText(String.valueOf(info[0].dalvikPss));

            memView = (TextView) convertView.findViewById(R.id.dalvikprivatedirty);
            memView.setText(String.valueOf(info[0].dalvikPrivateDirty));

            memView = (TextView) convertView.findViewById(R.id.dalvikshareddirty);
            memView.setText(String.valueOf(info[0].dalvikSharedDirty));

            memView = (TextView) convertView.findViewById(R.id.nativepss);
            memView.setText(String.valueOf(info[0].nativePss));

            memView = (TextView) convertView.findViewById(R.id.nativeprivatedirty);
            memView.setText(String.valueOf(info[0].nativePrivateDirty));

            memView = (TextView) convertView.findViewById(R.id.nativeshareddirty);
            memView.setText(String.valueOf(info[0].nativeSharedDirty));

            memView = (TextView) convertView.findViewById(R.id.otherpss);
            memView.setText(String.valueOf(info[0].otherPss));

            memView = (TextView) convertView.findViewById(R.id.otherprivatedirty);
            memView.setText(String.valueOf(info[0].otherPrivateDirty));

            memView = (TextView) convertView.findViewById(R.id.othershareddirty);
            memView.setText(String.valueOf(info[0].otherSharedDirty));

            final Button monitorPssButton = (Button) convertView.findViewById(R.id.monitorpss);
            if (servicepid == 0) {
                monitorPssButton.setText("Monitor Pss Usage");
                
                monitorPssButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    	if (servicetype != 0) 
                            Toast.makeText(MonitorActivity.this, "Only one monitor service can run at the same time. Please stop the other service befor starting a new one.", Toast.LENGTH_LONG).show();
                        else {
	                        Intent intent = new Intent(MonitorActivity.this, MonitorService.class);
	                        intent.putExtra("pid", pid[0]);
                            intent.putExtra("type", 1);
	                        servicepid = pid[0];
	                        servicetype = 1;
	                        MonitorAdapter.this.notifyDataSetChanged();
	                        startService(intent);
                        }
                    }
                });
                
            } else if (servicetype == 1) {
                if (servicepid == pid[0])
                    monitorPssButton.setText("Stop monitoring Pss Usage");
                else
                    monitorPssButton.setText("Stop monitoring Pss Usage of app with pid " + servicepid);
                monitorPssButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MonitorActivity.this, MonitorService.class);
                        servicepid = 0;
                        servicetype = 0;
                        MonitorAdapter.this.notifyDataSetChanged();

                        stopService(intent);
                    }
                });
            }

            final Button monitorPrivateButton = (Button) convertView.findViewById(R.id.monitorprivate);
            if (servicepid == 0) {
                monitorPrivateButton.setText("Monitor Private Mem Usage");
                
                monitorPrivateButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    	if (servicetype != 0) 
                            Toast.makeText(MonitorActivity.this, "Only one monitor service can run at the same time. Please stop the other service befor starting a new one.", Toast.LENGTH_LONG).show();
                        else {
	                        Intent intent = new Intent(MonitorActivity.this, MonitorService.class);
	                        intent.putExtra("pid", pid[0]);
                            intent.putExtra("type", 2);
	                        servicepid = pid[0];
	                        servicetype = 2;
	                        MonitorAdapter.this.notifyDataSetChanged();
	                        startService(intent);
                        }
                    }
                });
                
            } else if (servicetype == 2) {
                if (servicepid == pid[0])
                    monitorPrivateButton.setText("Stop monitoring Private Mem Usage");
                else
                    monitorPrivateButton.setText("Stop monitoring Private Mem Usage of app with pid " + servicepid);
                monitorPrivateButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MonitorActivity.this, MonitorService.class);
                        servicepid = 0;
                        servicetype = 0;
                        MonitorAdapter.this.notifyDataSetChanged();

                        stopService(intent);
                    }
                });
            }

            final Button monitorSharedButton = (Button) convertView.findViewById(R.id.monitorshared);
            if (servicepid == 0) {
                monitorSharedButton.setText("Monitor Shared Mem Usage");
                
                monitorSharedButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    	if (servicetype != 0) 
                            Toast.makeText(MonitorActivity.this, "Only one monitor service can run at the same time. Please stop the other service befor starting a new one.", Toast.LENGTH_LONG).show();
                        else {
	                        Intent intent = new Intent(MonitorActivity.this, MonitorService.class);
	                        intent.putExtra("pid", pid[0]);
                            intent.putExtra("type", 3);
	                        servicepid = pid[0];
	                        servicetype = 3;
	                        MonitorAdapter.this.notifyDataSetChanged();
	                        startService(intent);
                        }
                    }
                });
                
            } else if (servicetype == 3) {
                if (servicepid == pid[0])
                    monitorSharedButton.setText("Stop monitoring Shared Mem Usage");
                else
                    monitorSharedButton.setText("Stop monitoring Shared Mem Usage of app with pid " + servicepid);
                monitorSharedButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(MonitorActivity.this, MonitorService.class);
                        servicepid = 0;
                        servicetype = 0;
                        MonitorAdapter.this.notifyDataSetChanged();

                        stopService(intent);
                    }
                });
            }
            return convertView;
        }

        // Format importance in "human readable" format.
        public String getImportance(int importance) {
            switch (importance) {
                case RunningAppProcessInfo.IMPORTANCE_BACKGROUND: return "Background";
                case RunningAppProcessInfo.IMPORTANCE_EMPTY: return "Empty";
                case RunningAppProcessInfo.IMPORTANCE_FOREGROUND: return "Foreground";
                case RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE: return "Perceptible";
                case RunningAppProcessInfo.IMPORTANCE_SERVICE: return "Service";
                case RunningAppProcessInfo.IMPORTANCE_VISIBLE: return "Visible";
            }
            return "Other";
        }
    	
    	@Override
        public int getChildrenCount(int groupPostion) {
            // One view as child
            return 1;
        }

    	@Override
        public ActivityManager.RunningAppProcessInfo getGroup(int categoryPosition) {
            return (RunningAppProcessInfo)taskinfo[categoryPosition];
        }

    	@Override
        public int getGroupCount() {
            return taskinfo.length;
        }

        @Override
        public long getGroupId(int categoryPosition) {
            return categoryPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) MonitorActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.monitorgroup, null);
            }
        	TextView textView = (TextView) convertView.findViewById(R.id.appName);
			textView.setText(getGroup(groupPosition).processName);

			int[] pid = {getGroup(groupPosition).pid};
	        MemoryInfo[] info = activityManager.getProcessMemoryInfo(pid);

            textView = (TextView) convertView.findViewById(R.id.memUsage);
            DecimalFormat df = new DecimalFormat("###.##%");
            textView.setText(String.valueOf(df.format((float)info[0].getTotalPss()/totalMem)));

            return convertView;
        }
        
        public TextView getGenericView(){
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64);
			TextView textView = new TextView(MonitorActivity.this);
			textView.setLayoutParams(lp);
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			textView.setPadding(50, 0, 0, 0);
			return textView;
		}

        
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
