package com.view;

public class PasswordCharSequence implements CharSequence{

    private CharSequence mSource;

    public PasswordCharSequence(CharSequence charSequence){
        mSource = charSequence;
    }
    @Override
    public int length() {
        return mSource.length();
    }

    @Override
    public char charAt(int i) {
        return '*';
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        return mSource.subSequence(i,i1);
    }
}
