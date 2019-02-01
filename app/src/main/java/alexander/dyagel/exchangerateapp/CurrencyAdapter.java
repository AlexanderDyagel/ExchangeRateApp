package alexander.dyagel.exchangerateapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import alexander.dyagel.exchangerateapp.data.Bank;
import alexander.dyagel.exchangerateapp.db.data_obj.CurrencyRate;

public class CurrencyAdapter  extends RecyclerView.Adapter<CurrencyAdapter.MyViewHolder> {
    private List<CurrencyRate> currencyRateList;
    private Context context;

    public CurrencyAdapter(Context context, List<CurrencyRate> list){
        currencyRateList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        CurrencyRate currencyRate = currencyRateList.get(position);
        myViewHolder.rusIn.setText(currencyRate.getRateBuyRur());
        myViewHolder.rusOut.setText(currencyRate.getRateSellRur());
        myViewHolder.usdIn.setText(currencyRate.getRateBuyUsd());
        myViewHolder.usdOut.setText(currencyRate.getRateSellUsd());
        myViewHolder.euroIn.setText(currencyRate.getRateBuyEuro());
        myViewHolder.euroOut.setText(currencyRate.getRateSellEuro());
        myViewHolder.currentDate.setText(currencyRate.getDate());
        myViewHolder.pbLoad.setVisibility(View.INVISIBLE);

        switch (currencyRate.getBankId()){
            case Bank.BELAPB : {
                myViewHolder.setLogoBank(R.mipmap.logo_belapb);
                break;
            }
            case Bank.BELARUSBANK : {
               myViewHolder.setLogoBank(R.mipmap.belarusbanklogo);
               break;
            }
            case Bank.BELVEB : {
                myViewHolder.setLogoBank(R.mipmap.belveb_logo);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return currencyRateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView logoBank;
        private TextView currentDate;
        private TextView rusIn;
        private TextView rusOut;
        private TextView usdIn;
        private TextView usdOut;
        private TextView euroIn;
        private TextView euroOut;
        private ProgressBar pbLoad;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            logoBank = itemView.findViewById(R.id.iv_logo_bank);
            currentDate = itemView.findViewById(R.id.tv_current_date);
            rusIn = itemView.findViewById(R.id.tv_rus_in);
            rusOut = itemView.findViewById(R.id.tv_rus_out);
            usdIn = itemView.findViewById(R.id.tv_usd_in);
            usdOut = itemView.findViewById(R.id.tv_usd_out);
            euroIn = itemView.findViewById(R.id.tv_euro_in);
            euroOut = itemView.findViewById(R.id.tv_euro_out);
            pbLoad = itemView.findViewById(R.id.pb);
        }
        public void setLogoBank(int resource){
            logoBank.setImageResource(resource);
        }
    }
}
