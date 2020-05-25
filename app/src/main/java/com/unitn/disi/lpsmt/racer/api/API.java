package com.unitn.disi.lpsmt.racer.api;

import android.util.Log;

import com.auth0.android.jwt.BuildConfig;
import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.unitn.disi.lpsmt.racer.api.adapter.deserializer.JWTDeserializer;
import com.unitn.disi.lpsmt.racer.api.adapter.deserializer.LocalDateDeserializer;
import com.unitn.disi.lpsmt.racer.api.adapter.deserializer.LocalDateTimeDeserializer;
import com.unitn.disi.lpsmt.racer.api.adapter.serializer.JWTSerializer;
import com.unitn.disi.lpsmt.racer.api.adapter.serializer.LocalDateSerializer;
import com.unitn.disi.lpsmt.racer.api.adapter.serializer.LocalDateTimeSerializer;
import com.unitn.disi.lpsmt.racer.api.entity.error.ConflictError;
import com.unitn.disi.lpsmt.racer.api.entity.error.UnprocessableEntityError;
import com.unitn.disi.lpsmt.racer.api.interceptor.AdminInterceptor;
import com.unitn.disi.lpsmt.racer.api.interceptor.AuthInterceptor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API class
 *
 * @author Carlo Corradini
 */
public final class API {

    /**
     * {@link Log} TAG of this class
     */
    private static final String TAG = API.class.getName();

    /**
     * API base URL
     **/
    public static final String BASE_URL = "https://racer-2020.herokuapp.com/api/v1/";

    /**
     * Instance of the current {@link API} class assigned when the first {@link API#getInstance()} is called
     */
    private static API instance = null;

    /**
     * The {@link Retrofit} client instance
     */
    private final Retrofit client;

    /**
     * The {@link Retrofit} admin client instance
     */
    private final Retrofit adminClient;

    /**
     * Construct an API class.
     * API is constructed only once when the first {@link API#getInstance()} is called
     */
    private API() {
        client = buildClient();
        adminClient = buildAdminClient();

        Log.d(TAG, "Client builded");
        Log.i(TAG, "Initialized");
    }

    /**
     * Return the current {@link API} class instance.
     * The instance is constructed when this is the first call
     *
     * @return The {@link API} instance
     */
    public static API getInstance() {
        if (instance == null) {
            synchronized (API.class) {
                if (instance == null) {
                    instance = new API();
                }
            }
        }
        return instance;
    }

    /**
     * Build the {@link Retrofit} client
     *
     * @return The {@link Retrofit} client built
     */
    private Retrofit buildClient() {
        // OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC))
                .addInterceptor(new AuthInterceptor())
                .build();
        // END OkHttpClient

        // Gson
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(JWT.class, new JWTSerializer())
                .registerTypeAdapter(JWT.class, new JWTDeserializer())
                .create();
        // END Gson

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private Retrofit buildAdminClient() {
        // OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC))
                .addInterceptor(new AdminInterceptor())
                .build();
        // END OkHttpClient

        // Gson
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(JWT.class, new JWTSerializer())
                .registerTypeAdapter(JWT.class, new JWTDeserializer())
                .create();
        // END Gson

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /**
     * Return the {@link Retrofit} client instance
     *
     * @return {@link Retrofit} client instance
     */
    public Retrofit getClient() {
        return client;
    }

    /**
     * Return the {@link Retrofit} admin client instance
     *
     * @return {@link Retrofit} admin client instance
     */
    public Retrofit getAdminClient() {
        return adminClient;
    }

    /**
     * API Response entity mapping
     *
     * @param <T> The data type returned by the API data field
     * @author Carlo Corradini
     */
    public static final class Response<T> {
        /**
         * Response Status
         */
        public enum Status {
            /**
             * Success Response
             */
            @SerializedName("success")
            SUCCESS("success"),
            /**
             * Fail Response
             */
            @SerializedName("fail")
            FAIL("fail"),
            /**
             * Error Response
             */
            @SerializedName("error")
            ERROR("error");

            /**
             * Value of the Status
             */
            private final String value;

            /**
             * Construct a Status enum
             *
             * @param value Value of the Status
             */
            Status(String value) {
                this.value = value;
            }

            /**
             * Return the value of the current Status
             *
             * @return Status value
             */
            public String getValue() {
                return value;
            }
        }

        /**
         * API Response {@link Status}
         */
        @SerializedName("status")
        @Expose
        public Status status;

        /**
         * API Response success state
         */
        @SerializedName("is_success")
        @Expose
        public Boolean isSuccess;

        /**
         * API Response http status code
         */
        @SerializedName("status_code")
        @Expose
        public Short statusCode;

        /**
         * API Response http status code name
         */
        @SerializedName("status_code_name")
        @Expose
        public String statusCodeName;

        /**
         * API Response data field
         */
        @SerializedName("data")
        @Expose
        public T data;

        /**
         * Construct a Response class
         *
         * @param status         API Response {@link Status}
         * @param isSuccess      API Response success state
         * @param statusCode     API Response http status code
         * @param statusCodeName API Response http status code name
         * @param data           API Response data field
         */
        public Response(Status status, Boolean isSuccess, Short statusCode, String statusCodeName, T data) {
            this.status = status;
            this.isSuccess = isSuccess;
            this.statusCode = statusCode;
            this.statusCodeName = statusCodeName;
            this.data = data;
        }

        /**
         * Construct an empty Response class
         */
        public Response() {
        }
    }

    /**
     * Error Converter for failed {@link retrofit2.Call call}
     *
     * @author Carlo Corradini
     */
    public static final class ErrorConverter {

        /**
         * {@link Log} TAG of this class
         */
        private static final String TAG = ErrorConverter.class.getName();

        /**
         * {@link API.Response#data} is empty type
         */
        public static final Type TYPE_EMPTY = new TypeToken<API.Response>() {
        }.getType();

        /**
         * {@link API.Response#data} is {@link JWT} type
         */
        public static final Type TYPE_JWT = new TypeToken<API.Response<JWT>>() {
        }.getType();

        /**
         * {@link API.Response#data} is {@link UUID} type
         */
        public static final Type TYPE_UUID = new TypeToken<API.Response<UUID>>() {
        }.getType();

        /**
         * {@link API.Response#data} is a {@link List} of {@link UnprocessableEntityError} type
         */
        public static final Type TYPE_UNPROCESSABLE_ENTITY_LIST = new TypeToken<API.Response<List<UnprocessableEntityError>>>() {
        }.getType();

        /**
         * {@link API.Response#data} is a {@link List} of {@link ConflictError} type
         */
        public static final Type TYPE_CONFLICT_LIST = new TypeToken<API.Response<List<ConflictError>>>() {
        }.getType();

        /**
         * Convert the {@link ResponseBody error body} into an {@link API.Response} with {@link API.Response#data} as type type
         *
         * @param errorBody The {@link ResponseBody error body}
         * @param type      The {@link API.Response#data} type
         * @param <T>       {@link API.Response#data} type, the conversion is implicit
         * @return The converted {@link ResponseBody error body} as an {@link API.Response<T>}
         */
        public static <T> API.Response<T> convert(ResponseBody errorBody, Type type) {
            if (errorBody == null || type == null) return null;

            Converter<ResponseBody, API.Response<T>> converter = API.getInstance().getClient().responseBodyConverter(type, new Annotation[0]);
            API.Response<T> error = null;

            try {
                error = converter.convert(errorBody);
            } catch (IOException ex) {
                Log.e(TAG, "Unable to convert error body due to " + ex.getMessage());
            }

            return error;
        }

        /**
         * Convert the {@link ResponseBody error body} into an {@link API.Response} with an empty {@link API.Response#data}
         *
         * @param errorBody The {@link ResponseBody error body}
         * @param <T>       {@link API.Response#data} type, the conversion is implicit
         * @return The converted {@link ResponseBody error body} as an {@link API.Response<T>}
         */
        public static <T> API.Response<T> convert(ResponseBody errorBody) {
            return convert(errorBody, TYPE_EMPTY);
        }
    }
}