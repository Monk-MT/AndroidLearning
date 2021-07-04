package com.cmt.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-04-22-16:14
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;
    private HashMap<UUID, Integer> mCrimesIds;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            synchronized (CrimeLab.class) {
                if (sCrimeLab == null) {
                    sCrimeLab = new CrimeLab(context);
                }
            }
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
        mCrimesIds = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setId(UUID.randomUUID());
            mCrimesIds.put(crime.getId(), i);
            crime.setTitle("Crime #" + i);
            crime.setDate(new Date());
            crime.setSolved(i % 2 == 0); // Every other one
            mCrimes.add(crime);
        }
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
//        for (Crime crime : mCrimes) {
//            if (crime.getId().equals(id)) {
//                return crime;
//            }
//        }
        if (mCrimesIds.containsKey(id)) {
            return mCrimes.get(mCrimesIds.get(id));
        }
        return null;
    }
}
