package id.mobilecomputing.uasmc2020;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<ContactsGetSet> contactsGetSetList;
    private Context mContext;
    public ContactsAdapter(List<ContactsGetSet> contactsGetSetList, Context mContext){
        this.contactsGetSetList = contactsGetSetList;
        this.mContext = mContext;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.contacts_view, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        ContactsGetSet contactsGetSet = contactsGetSetList.get(position);
        holder.tv_ContactName.setText(contactsGetSet.getContactName());
        holder.tv_ContactNumber.setText((CharSequence) contactsGetSet.getContactNumber());
    }

    @Override
    public int getItemCount() {
        return contactsGetSetList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{
        TextView tv_ContactName;
        TextView tv_ContactNumber;

        public ContactViewHolder(View itemView){
            super(itemView);
            tv_ContactName = (TextView) itemView.findViewById(R.id.tv_contactName);
            tv_ContactNumber = (TextView) itemView.findViewById(R.id.tv_contactNumber);
        }
    }
}
