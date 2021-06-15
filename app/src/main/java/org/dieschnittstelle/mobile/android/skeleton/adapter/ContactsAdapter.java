package org.dieschnittstelle.mobile.android.skeleton.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.dieschnittstelle.mobile.android.skeleton.DetailViewActivity;
import org.dieschnittstelle.mobile.android.skeleton.R;
import org.dieschnittstelle.mobile.android.skeleton.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends ArrayAdapter<Contact> {
    private int layoutResource;
    private DetailViewActivity controller;

    public ContactsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Contact> objects, DetailViewActivity controller) {
        super(context, resource, objects);
        this.layoutResource = resource;
        this.controller = controller;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        View itemView = null;
        Contact currentContact = getItem(position);

        if (view != null) {
            View textView = view.findViewById(R.id.rowContactsView);
            if (textView != null) {

            }

            itemView = view;
        } else {
            ViewGroup viewGroup = (ViewGroup) view;
            View inflatedView = View.inflate(controller, layoutResource, viewGroup);
            TextView textView = (TextView) inflatedView.findViewById(R.id.rowContactsView);
            textView.setText(currentContact.getName());

            //Action for SMS contact
            ImageView imageViewCall = (ImageView) inflatedView.findViewById(R.id.messageButton);
            imageViewCall.setOnClickListener(v -> {
                if (currentContact.getNumbers().size() == 1 && currentContact.getNumbers() != null) {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.setData(Uri.parse("smsto:" + currentContact.getNumbers().get(0)));
                    smsIntent.putExtra("sms_body", controller.getTodo().getName() + ": " + controller.getTodo().getDescription());
                    controller.startActivity(smsIntent);
                } else if (currentContact.getNumbers().size() > 1 && currentContact.getNumbers() != null) {
                    String[] type = new String[0];
                    final AlertDialog.Builder builder = new AlertDialog.Builder(controller);
                    String[] selectedItem = {""};

                    builder.setTitle("Nummer auswählen:");

                    final List<String> numbers = currentContact.getNumbers();

                    builder.setSingleChoiceItems(
                            (String[]) numbers.toArray(type),
                            -1,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    selectedItem[0] = numbers.get(i);

                                }
                            });

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                            smsIntent.setData(Uri.parse("smsto:" + currentContact.getNumbers().get(0)));
                            smsIntent.putExtra("sms_body", controller.getTodo().getName() + ": " + controller.getTodo().getDescription());
                            controller.startActivity(smsIntent);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(getContext(), "Keine Nummer hinterlegt", Toast.LENGTH_SHORT).show();
                }

            });

            //Action for E-Mail contact
            ImageView imageViewMessage = (ImageView) inflatedView.findViewById(R.id.mailButton);
            imageViewMessage.setOnClickListener(v -> {
                if (currentContact.getEmails().size() == 1 && currentContact.getEmails() != null) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{currentContact.getEmails().get(0)});
                    email.putExtra(Intent.EXTRA_SUBJECT, controller.getTodo().getName());
                    email.putExtra(Intent.EXTRA_TEXT, controller.getTodo().getDescription());
                    email.setType("message/rfc822");
                    controller.startActivity(Intent.createChooser(email, "Wähle einen E-Mail Client :"));

                } else if (currentContact.getEmails().size() > 1 && currentContact.getEmails() != null) {
                    String[] type = new String[0];
                    final AlertDialog.Builder builder = new AlertDialog.Builder(controller);
                    String[] selectedItem = {""};

                    builder.setTitle("E-Mail Adresse auswählen:");

                    final List<String> emails = currentContact.getEmails();

                    builder.setSingleChoiceItems(
                            (String[]) emails.toArray(type),
                            -1,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    selectedItem[0] = emails.get(i);

                                }
                            });

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_EMAIL, new String[]{selectedItem[0]});
                            email.putExtra(Intent.EXTRA_SUBJECT, controller.getTodo().getName());
                            email.putExtra(Intent.EXTRA_TEXT, controller.getTodo().getDescription());
                            email.setType("message/rfc822");
                            controller.startActivity(Intent.createChooser(email, "Wähle einen E-Mail Client :"));
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(getContext(), "Keine E-Mail Adresse hinterlegt", Toast.LENGTH_SHORT).show();
                }

            });

            //Action for contact delete
            ImageView imageViewDelete = (ImageView) inflatedView.findViewById(R.id.deleteContact);
            imageViewDelete.setOnClickListener(v -> {

                AlertDialog deleteContact = new AlertDialog.Builder(controller)
                        // set message, title, and icon
                        .setTitle("Kontakt entfernen")
                        .setMessage("Möchtest du den verknüpften Kontakt " + "'" + currentContact.getName() + "'" +" wirklich entfernen?")

                        .setPositiveButton("Entfernen", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                controller.getTodo().getContacts().remove(String.valueOf(currentContact.getId()));
                                remove(currentContact);
                                dialog.dismiss();
                            }

                        })
                        .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })
                        .create();

                deleteContact.show();
                    });

            itemView = inflatedView;

        }

        return itemView;
    }


}

