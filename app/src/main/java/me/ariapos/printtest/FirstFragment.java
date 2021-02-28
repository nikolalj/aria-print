package me.ariapos.printtest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import me.ariapos.print.AriaPrint;

import static me.ariapos.printtest.R.id.button_first;

public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final AriaPrint ariaPrint = new AriaPrint(getActivity(), 48,32,203,null, 30);

        view.findViewById(button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ariaPrint.print(
                    "================================\n" +
                        "           Beer House\n" +
                        "          13. jul br 1\n" +
                        "               ME\n" +
                        "\n" +
                        "PIB: 12345678\n" +
                        "--------------------------------\n" +
                        "2020-12-24 19:19:31\n" +
                        "Zaposleni: Nikola Ljumovic\n" +
                        "--------------------------------\n" +
                        "2x Banana                   1.78\n" +
                        "    Popust (25%)           -0.45\n" +
                        "    Sa popustom             1.34\n" +
                        "1x Cigari sivi              3.00\n" +
                        "1x Zavjesa amber          244.00\n" +
                        "2x Banana                   1.78\n" +
                        "    Popust (25%)           -0.45\n" +
                        "    Sa popustom             1.34\n" +
                        "1x Cigari sivi              3.00\n" +
                        "1x Zavjesa amber          244.00\n" +
                        "2x Banana                   1.78\n" +
                        "    Popust (25%)           -0.45\n" +
                        "    Sa popustom             1.34\n" +
                        "1x Cigari sivi              3.00\n" +
                        "1x Zavjesa amber          244.00\n" +
                        "2x Banana                   1.78\n" +
                        "    Popust (25%)           -0.45\n" +
                        "    Sa popustom             1.34\n" +
                        "--------------------------------\n" +
                        "Stopa A (21.00%):        4243.11\n" +
                        "Ukupan porez:            4243.11\n" +
                        "--------------------------------\n" +
                        "Ukupno:                   248.78\n" +
                        "Za uplatu:                248.34\n" +
                        "================================\n" +
                        "IKOF: 08CB97A9350EAF11F80165840.",
                    "https://efitest.tax.gov.me/ic/#/verify?iic=5FD4CC52B14E670119FF7AD80D6389D2&tin=03091627&crtd=2021-01-14T19:09:10+01:00&ord=1&bu=yl349mn504&cr=zy141mk648&sw=cx878wj457&prc=1.17"
                );

//                Snackbar.make(view, "PRINTED", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
            }
        });
    }
}
