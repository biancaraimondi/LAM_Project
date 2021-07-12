package com.example.trackingmypantry;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;

public class DispensaFragment extends Fragment {

    private ArrayAdapter<Product> mAdapter;
    private ListView mListView;
    private TextInputLayout textBarcodeLayout;

    private void setmAdapter(Context context, List<Product> productsList) {
        mAdapter = new ArrayAdapter<Product>(context, R.layout.card_relative_layout, productsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater inflater = getLayoutInflater();
                View itemView = inflater.inflate(R.layout.card_relative_layout, null, true);

                TextView title = (TextView) itemView.findViewById(R.id.card_title);
                title.setText(productsList.get(position).name);

                TextView secondaryText = (TextView) itemView.findViewById(R.id.card_secondary_text);
                secondaryText.setText(productsList.get(position).description);

                TextView quantityText = (TextView) itemView.findViewById(R.id.card_quantity);
                quantityText.setText(productsList.get(position).quantity.toString());

                if (productsList.get(position).image != null) {
                    byte[] decodedString = Base64.decode(productsList.get(position).image, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ImageView iv_icon = (ImageView) itemView.findViewById(R.id.card_image);
                    iv_icon.setImageBitmap(decodedByte);
                }
                return itemView;
            }
        };

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showQuantityDialog(mAdapter.getItem(i));
            }
        });
    }

    public void setProductListToFind(String nameToFind){
        EditText textBarcode = textBarcodeLayout.getEditText();
        if (textBarcode.getText().toString().matches("") == false) {
            ArrayList<Product> mProductListByName = new ArrayList<Product>();
            for (Product element: ((SelectionActivity) getActivity()).mProductsViewModel.getListOfProductsDispensa()){
                String substring = element.name.substring(0, nameToFind.length());
                Log.i("SUBSTRING", "substring " + substring + " name to find " + nameToFind);
                if(substring.toLowerCase().matches(nameToFind.toLowerCase())){
                    mProductListByName.add(element);
                }
            }
            setmAdapter(getContext(), mProductListByName);
        } else {
            setmAdapter(getContext(), ((SelectionActivity) getActivity()).mProductsViewModel.getListOfProductsDispensa());
        }
    }


    private void showQuantityDialog(Product product) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);

        Button quantityText = dialog.findViewById(R.id.textNumQuantity);
        quantityText.setText(Integer.toString(product.quantity));

        Button meno = dialog.findViewById(R.id.buttonMeno);
        Button piu = dialog.findViewById(R.id.buttonPiu);
        Button eliminaProdotto = dialog.findViewById(R.id.dialog_button_eliminaProdotto);

        meno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt((String) quantityText.getText()) > 1){
                    ((SelectionActivity) getActivity()).productRepository.removeQuantityProduct(product);
                    quantityText.setText(Integer.toString(
                            Integer.parseInt((String) quantityText.getText())
                                    - 1
                            )
                    );
                }
            }
        });

        piu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SelectionActivity) getActivity()).productRepository.addQuantityProduct(product);
                quantityText.setText(Integer.toString(
                        Integer.parseInt((String) quantityText.getText())
                                + 1
                        )
                );
            }
        });

        eliminaProdotto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SelectionActivity) getActivity()).productRepository.deleteProduct(product);
                dialog.dismiss();
            }
        });

        Button ok = dialog.findViewById(R.id.dialog_button_OK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public DispensaFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dispensa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = getActivity().findViewById(R.id.card_list_view);
        textBarcodeLayout = getActivity().findViewById(R.id.textBarcode);

        if ( ((SelectionActivity) getActivity()).mProductsViewModel.getProductNameToFind() != null){
            textBarcodeLayout.getEditText().setText( ((SelectionActivity) getActivity()).mProductsViewModel.getProductNameToFind() );
        }

        EditText textBarcode = textBarcodeLayout.getEditText();
        textBarcode.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((SelectionActivity) getActivity()).mProductsViewModel.setProductNameToFind(s.toString());
                setProductListToFind(s.toString());
            }
        });

        ((SelectionActivity) getActivity()).productRepository.getAllProducts().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                if (((SelectionActivity) getActivity()).mProductsViewModel.getListOfProductsDispensa() == null
                        || (((SelectionActivity) getActivity()).mProductsViewModel.getListOfProductsDispensa() != null && textBarcode.getText().toString().matches(""))
                ){
                    ((SelectionActivity) getActivity()).mProductsViewModel.setListOfProductsDispensa((ArrayList<Product>) products);
                    setmAdapter(getContext(), ((SelectionActivity) getActivity()).mProductsViewModel.getListOfProductsDispensa());
                } else {
                    String textBarcode = textBarcodeLayout.getEditText().getText().toString();
                    setProductListToFind(textBarcode);
                }

            }
        });
    }

}