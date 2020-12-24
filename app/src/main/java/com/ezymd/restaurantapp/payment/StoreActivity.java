package com.ezymd.restaurantapp.payment;

import androidx.annotation.NonNull;

import com.ezymd.restaurantapp.R;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.model.Address;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.ShippingInformation;
import com.stripe.android.model.ShippingMethod;
import com.stripe.android.view.ShippingInfoWidget;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class StoreActivity {

    @NonNull
    public static PaymentSessionConfig createPaymentSessionConfig() {
        return new PaymentSessionConfig.Builder()

                // hide the phone field on the shipping information form
                .setHiddenShippingInfoFields(
                        ShippingInfoWidget.CustomizableShippingField.Phone
                )

                // make the address line 2 field optional
                .setOptionalShippingInfoFields(
                        ShippingInfoWidget.CustomizableShippingField.Line2
                )

                // specify an address to pre-populate the shipping information form
                .setPrepopulatedShippingInfo(new ShippingInformation(
                        new Address.Builder()
                                .setLine1("123 Market St")
                                .setCity("San Francisco")
                                .setState("CA")
                                .setPostalCode("94107")
                                .setCountry("US")
                                .build(),
                        "Jenny Rosen",
                        "4158675309"
                ))

                // collect shipping information
                .setShippingInfoRequired(true)

                // collect shipping method
                .setShippingMethodsRequired(false)

                // specify the payment method types that the customer can use;
                // defaults to PaymentMethod.Type.Card
                .setPaymentMethodTypes(
                        Arrays.asList(PaymentMethod.Type.Card)
                )

                // only allow US and Canada shipping addresses
                .setAllowedShippingCountryCodes(new HashSet<>(
                        Arrays.asList("US", "CA")
                ))

                // specify a layout to display under the payment collection form
                .setAddPaymentMethodFooter(R.layout.add_payment_method_footer)

                // specify the shipping information validation delegate
                .setShippingInformationValidator(new AppShippingInformationValidator())

                // specify the shipping methods factory delegate
            //    .setShippingMethodsFactory(new AppShippingMethodsFactory())

                // if `true`, will show "Google Pay" as an option on the
                // Payment Methods selection screen
                .setShouldShowGooglePay(false)

                .build();
    }

    private static class AppShippingInformationValidator
            implements PaymentSessionConfig.ShippingInformationValidator {

        @Override
        public boolean isValid(
                @NonNull ShippingInformation shippingInformation
        ) {
            final Address address = shippingInformation.getAddress();
            return address != null && Locale.US.getCountry().equals(address.getCountry());
        }

        @NonNull
        public String getErrorMessage(
                @NonNull ShippingInformation shippingInformation
        ) {
            return "A US address is required";
        }
    }

    private static class AppShippingMethodsFactory
            implements PaymentSessionConfig.ShippingMethodsFactory {

        @Override
        public List<ShippingMethod> create(
                @NonNull ShippingInformation shippingInformation
        ) {
            return Arrays.asList(
                    new ShippingMethod(
                            "UPS Ground",
                            "ups-ground",
                            0,
                            "USD",
                            "Arrives in 3-5 days"
                    ),
                    new ShippingMethod(
                            "FedEx",
                            "fedex",
                            599,
                            "USD",
                            "Arrives tomorrow"
                    )
            );
        }
    }
}
