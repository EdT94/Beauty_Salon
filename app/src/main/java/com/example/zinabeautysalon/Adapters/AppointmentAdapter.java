package com.example.zinabeautysalon.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zinabeautysalon.MainActivity;
import com.example.zinabeautysalon.Models.Appointment;
import com.example.zinabeautysalon.R;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AppointmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Appointment> appointments = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference appointmentsRef;

    public AppointmentAdapter(Context context, ArrayList<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_appointment_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AppointmentViewHolder appointmentViewHolder = (AppointmentViewHolder) holder;
        Appointment appointment = getItem(position);
        if (appointment.getApproved())
            appointmentViewHolder.appointments_BTN_wait.setVisibility(View.GONE);
        appointmentViewHolder.appointments_TXT_appointment.setText(appointment.toString());
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    private Appointment getItem(int position) {
        return appointments.get(position);
    }


    public class AppointmentViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView appointments_TXT_appointment;
        private ImageButton appointments_BTN_wait;


        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            appointments_TXT_appointment = itemView.findViewById(R.id.appointments_TXT_appointment);
            appointments_BTN_wait = itemView.findViewById(R.id.appointments_BTN_wait);

            itemView.findViewById(R.id.appointments_BTN_remove).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(itemView.getContext(), R.style.AlertDialogCustom));
                            builder.setMessage("אתה בטוח שאתה רוצה לבטל את התור?");
                            builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    firebaseDatabase = FirebaseDatabase.getInstance();
                                    appointmentsRef = firebaseDatabase.getReference("Appointments");
                                    appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot child : snapshot.getChildren()) {
                                                try {
                                                    Appointment appointment = child.getValue(Appointment.class);
                                                    if (appointment.equals(appointments.get(getAdapterPosition()))) {
                                                        appointments.remove(getAdapterPosition());
                                                        child.getRef().removeValue();
                                                        notifyItemRemoved(getAdapterPosition());
                                                        notifyItemRangeChanged(getAdapterPosition(), getItemCount());
                                                        break;
                                                    }
                                                } catch (Exception e) {
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }


                            });
                            builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
            );

            itemView.findViewById(R.id.appointments_BTN_wait).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.getIsManagerConnected() == true) {
                        if (appointments.get(getAdapterPosition()).getApproved() == false) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(itemView.getContext(), R.style.AlertDialogCustom));
                            builder.setMessage("אתה בטוח שאתה רוצה לאשר את התור?");
                            builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    firebaseDatabase = FirebaseDatabase.getInstance();
                                    appointmentsRef = firebaseDatabase.getReference("Appointments");
                                    appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot child : snapshot.getChildren()) {
                                                try {
                                                    Appointment appointment = child.getValue(Appointment.class);
                                                    if (appointment.equals(appointments.get(getAdapterPosition()))) {
                                                        HashMap<String, Object> result = new HashMap<>();
                                                        result.put("approved", true);
                                                        FirebaseDatabase.getInstance().getReference().child("Appointments").child(child.getRef().getKey()).updateChildren(result);
                                                        Toast.makeText(itemView.getContext(), "התור אושר בהצלחה", Toast.LENGTH_SHORT).show();
                                                        itemView.findViewById(R.id.appointments_BTN_wait).setVisibility(View.GONE);
                                                        //hideSystemUI();
                                                        break;
                                                    }
                                                } catch (Exception e) {
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }


                            });
                            builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    } else {
                        Toast.makeText(itemView.getContext(), "ממתין לאישור מנהל", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}