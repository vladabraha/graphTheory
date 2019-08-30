package cz.uhk.graphstheory.statistics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cz.uhk.graphstheory.R;
import cz.uhk.graphstheory.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> users;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class UserViewHolder extends RecyclerView.ViewHolder {

        //tady si vytahneme veci z xml, ktery budeme chtit pro kazdej zaznam zmenit
        TextView userName, userScore, userTeam;

        public UserViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.txt_userName);
            userScore = view.findViewById(R.id.txt_userScore);
            userTeam = view.findViewById(R.id.txt_userTeam);
        }
    }

    // Konstruktor pro předání dat, která budeme zobrazovat
    public UserAdapter(ArrayList<User> users) {
        this.users = users;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        // ukaž na view, který se bude opakovat (jeden radek RecyclerView)
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_statistic_row, parent, false);

        return new UserViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        // - get element from your dataset at this position
        // tady nasetuj data pro jeden radek z listu dat, ktera mame k dispozici
//        holder.textView.setText(mDataset[position]);
        User user = users.get(position);
        holder.userName.setText(user.getNickName());
        holder.userScore.setText(String.valueOf(user.getScore()));
        holder.userTeam.setText(user.getTeam());

//        holder.userName.setText("tes");
//        holder.userScore.setText("text2");
//        holder.userTeam.setText("text3");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateData(ArrayList<User> users){
        this.users = users;
    }

}
