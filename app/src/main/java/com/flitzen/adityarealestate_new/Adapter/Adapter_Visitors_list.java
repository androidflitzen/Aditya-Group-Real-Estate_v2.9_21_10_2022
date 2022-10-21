package com.flitzen.adityarealestate_new.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flitzen.adityarealestate_new.Activity.Activity_Edit_Visitors;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.Item_Visitors_list;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Adapter_Visitors_list extends RecyclerView.Adapter<Adapter_Visitors_list.ViewHolder> {
    ArrayList<Item_Visitors_list> itemList = new ArrayList<>();
    Activity context;
    ProgressDialog prd;
    OnItemClickListener mItemClickListener;
    // OnItemClickListener listener;

    public Adapter_Visitors_list(Activity context, ArrayList<Item_Visitors_list> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adaptet_visitors_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txt_cust_name.setText(itemList.get(position).getName());
        holder.txt_cust_no.setText(itemList.get(position).getContact_no());
        holder.txt_address.setText(itemList.get(position).getAddress());
        holder.txt_remarks.setText(itemList.get(position).getRemark());
        holder.txt_date.setText(itemList.get(position).getDate());

//        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //    view.getContext().startActivity(new Intent(context, Activity_Edit_Visitors.class));
//                // context.startActivity(new Intent(context, Activity_Edit_Visitors.class));
//                Intent intent = new Intent(context, Activity_Edit_Visitors.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//            }
//        });

        //  holder.bind(position, listener);
//
//        holder.view_main.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mItemClickListener != null)
//                    mItemClickListener.onItemClick(position);
//            }
//        });
//        holder.liMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mItemClickListener != null)
//                    mItemClickListener.onItemClick(position);
//            }
//        });


//        holder.liMain.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if (mItemClickListener != null)
//                    mItemClickListener.onItemClick(position);
//                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext())
//                        .setTitle("Delete Visitor")
//                        .setMessage("Are you sure want to delete?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //showPrd();
//                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//                                databaseReference.child("Visitors").child(itemList.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        if (snapshot.getValue() != null) {
//                                            databaseReference.child("Visitors").child(itemList.get(position).getId()).removeValue().addOnCompleteListener(context, new OnCompleteListener<Void>() {
//                                                @SuppressLint("NotifyDataSetChanged")
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    hidePrd();
//                                                    if (task.isSuccessful()) {
//                                                        notifyDataSetChanged();
//                                                        new CToast(context).simpleToast("Visitors delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
//                                                    } else {
//                                                        new CToast(context).simpleToast(task.getException().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
//                                                    }
//
//                                                }
//                                            }).addOnFailureListener(context, new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    hidePrd();
//                                                    new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
//                                                }
//                                            });
//                                        } else {
//                                            hidePrd();
//                                            new CToast(context).simpleToast("Visitors not exist", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//                                        hidePrd();
//                                        new CToast(context).simpleToast(error.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
//                                    }
//                                });
//
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
//                builder.show();
//                return true;
//            }
//        });
        holder.iv_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Activity_Edit_Visitors.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //    intent.putExtra("txt_cust_name",);
                intent.putExtra("visitor",itemList.get(position));
                context.startActivity(intent);
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openDeleteDialog(position);
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        TextView textView = new TextView(context);
//        textView.setText("If you delete this Visitors.");
//        textView.setTextSize(15);
//        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
////            textView.setTextColor(context.getResources().getColor(R.color.msg_fail));
//        textView.setPadding(30, 15, 30, 5);
//        builder.setMessage("Delete Visitors ?");
//        builder.setCustomTitle(textView);
//        builder.setCancelable(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Delete Visitor")
                        .setMessage("Are you sure want to delete?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // showPrd();
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.child("Visitors").child(itemList.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.getValue() != null) {
                                                    databaseReference.child("Visitors").child(itemList.get(position).getId()).removeValue().addOnCompleteListener(context, new OnCompleteListener<Void>() {
                                                        @SuppressLint("NotifyDataSetChanged")
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            //   hidePrd();
                                                            if (task.isSuccessful()) {
                                                                itemList.remove(position);
                                                                notifyDataSetChanged();
                                                                new CToast(context).simpleToast("Visitors delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                            } else {
                                                                new CToast(context).simpleToast(task.getException().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                            }

                                                        }
                                                    }).addOnFailureListener(context, new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            hidePrd();
                                                            new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                        }
                                                    });
                                                } else {
                                                    hidePrd();
                                                    new CToast(context).simpleToast("Visitors not exist", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                hidePrd();
                                                new CToast(context).simpleToast(error.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                            }
                                        });
                                        // deleteEntryAPI(position);
                                    }
                                });


                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });


    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_cust_name, txt_cust_no, txt_address, txt_remarks, txt_date;
        CardView view_main;
        ImageView ivDelete,iv_Edit;
        LinearLayout liMain;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_cust_name = (TextView) itemView.findViewById(R.id.txt_cust_name);
            txt_cust_no = (TextView) itemView.findViewById(R.id.txt_cust_no);
            txt_address = (TextView) itemView.findViewById(R.id.txt_address);
            view_main = (CardView) itemView.findViewById(R.id.view_main);
            ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            iv_Edit = (ImageView) itemView.findViewById(R.id.iv_Edit);
            liMain = itemView.findViewById(R.id.liMain);
            txt_remarks = (TextView) itemView.findViewById(R.id.txt_remarks);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);

        }

        public void bind(final int position, final Adapter_Plot_Payment_List.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

    }

    public void OnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

//    public void setOnItemListener(OnItemClickListener listener) {
//        this.listener = listener;
//    }


//    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
//        this.mItemClickListener = mItemClickListener;
//    }
//
//    public interface OnItemClickListener {
//
//        void onItemClick(int position);
//    }

//    private void openDeleteDialog(final int position) {
////        AlertDialog.Builder builder = new AlertDialog.Builder(context);
////        TextView textView = new TextView(context);
////        textView.setText("If you delete this Visitors.");
////        textView.setTextSize(15);
////        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
//////            textView.setTextColor(context.getResources().getColor(R.color.msg_fail));
////        textView.setPadding(30, 15, 30, 5);
////        builder.setMessage("Delete Visitors ?");
////        builder.setCustomTitle(textView);
////        builder.setCancelable(true);
//        AlertDialog.Builder builder = new AlertDialog.Builder(context)
//                .setTitle("Delete Visitor")
//                .setMessage("Are you sure want to delete?")
//                .setPositiveButton("Yes",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // showPrd();
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//                        databaseReference.child("Visitors").child(itemList.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot.getValue() != null) {
//                                    databaseReference.child("Visitors").child(itemList.get(position).getId()).removeValue().addOnCompleteListener(context, new OnCompleteListener<Void>() {
//                                        @SuppressLint("NotifyDataSetChanged")
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            hidePrd();
//                                            if (task.isSuccessful()) {
//                                                notifyDataSetChanged();
//                                                new CToast(context).simpleToast("Visitors delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
//                                            } else {
//                                                new CToast(context).simpleToast(task.getException().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
//                                            }
//
//                                        }
//                                    }).addOnFailureListener(context, new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            hidePrd();
//                                            new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
//                                        }
//                                    });
//                                } else {
//                                    hidePrd();
//                                    new CToast(context).simpleToast("Visitors not exist", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                hidePrd();
//                                new CToast(context).simpleToast(error.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
//                            }
//                        });
//                        // deleteEntryAPI(position);
//                    }
//                });
//
//
//        builder.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }

    public void showPrd() {
//        if (progressDialog != null && !progressDialog.isShowing()) {
//            progressDialog.show();
//        }
        prd = new ProgressDialog(context);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
        prd.dismiss();
    }
}
