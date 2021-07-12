package com.example.trackingmypantry;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class ProductsViewModel extends ViewModel {

    private ArrayList<Product> mProductsListSpesa;
    private String imageNewProduct;
    private String nameNewProduct;
    private String descriptionNewProduct;
    private String barcodeToFind;
    private String userId;
    private ArrayList<Product> mProductListDispensa;
    private String productNameToFind;


    public ArrayList<Product> getListOfProductsSpesa() {
        if (mProductsListSpesa == null) {
            mProductsListSpesa = new ArrayList<Product>();
        }
        Log.i("GET PRODUCTS SPESA","Products List get");
        return mProductsListSpesa;
    }

    public void setListOfProductsSpesa(ArrayList<Product> list) {
        Log.i("SET PRODUCTS SPESA","Products List set");
        mProductsListSpesa = list;
    }

    public ArrayList<Product> getListOfProductsDispensa() {
        Log.i("GET PRODUCTS DISPENSA","Products List get");
        return mProductListDispensa;
    }

    public void setListOfProductsDispensa(ArrayList<Product> list) {
        Log.i("SET PRODUCTS DISPENSA","Products List set");
        mProductListDispensa = list;
    }

    public String getUserId() {
        Log.i("GET USERID","UserId get");
        return userId;
    }

    public void setUserId(String mUserId) {
        Log.i("SET USERID","UserId set");
        userId = mUserId;
    }

    public String getImage() {
        Log.i("GET IMAGE","Image encoded get");
        return imageNewProduct;
    }

    public void setImage(String imageEncoded) {
        Log.i("SET IMAGE","Image encoded set");
        imageNewProduct = imageEncoded;
    }

    public String[] getNameDescriptionNewProduct() {
        Log.i("GET NAME_DESCRIPTION","Name and description get");
        return new String[] {nameNewProduct, descriptionNewProduct};
    }

    public void setNameDescriptionNewProduct(String name, String description) {
        Log.i("SET NAME_DESCRIPTION","Name and description set");
        nameNewProduct = name;
        descriptionNewProduct = description;
    }

    public String getBarcodeSpesa() {
        Log.i("GET BARCODE","Barcode get");
        return barcodeToFind;
    }

    public void setBarcodeSpesa(String barcode) {
        Log.i("SET BARCODE","Barcode set");
        barcodeToFind = barcode;
    }

    public String getProductNameToFind() {
        Log.i("GET PRODUCT NAME","Product name to find get");
        return productNameToFind;
    }

    public void setProductNameToFind(String name) {
        Log.i("SET PRODUCT NAME","Product name to find set");
        productNameToFind = name;
    }

    public ProductsViewModel() {
        super();
        imageNewProduct = null;
        nameNewProduct = null;
        descriptionNewProduct = null;
        barcodeToFind = null;
        productNameToFind = null;
    }

}
