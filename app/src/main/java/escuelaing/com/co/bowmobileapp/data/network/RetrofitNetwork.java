package escuelaing.com.co.bowmobileapp.data.network;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import escuelaing.com.co.bowmobileapp.data.entities.LoginWrapper;
import escuelaing.com.co.bowmobileapp.data.entities.Party;
import escuelaing.com.co.bowmobileapp.data.entities.Token;
import escuelaing.com.co.bowmobileapp.data.entities.User;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitNetwork
        implements Network
{
    //Corriendo el app desde localhost (cambiar la direccion ip por la direccion de localhost pero sin poner localhost o 127.0.0.1)
    //private static final String BASE_URL = "http://192.168.0.3:8080/";

    //Conectandose al deploy de Heroku.
    private static final String BASE_URL = "https://farrapp-api.herokuapp.com";

    private NetworkService networkService;

    private ExecutorService backgroundExecutor = Executors.newFixedThreadPool( 1 );

    public RetrofitNetwork()
    {
        Retrofit retrofit =
                new Retrofit.Builder().baseUrl( BASE_URL ).addConverterFactory( GsonConverterFactory.create() ).build();
        networkService = retrofit.create( NetworkService.class );
    }

    @Override
    public void login(final LoginWrapper loginWrapper, final RequestCallback<Token> requestCallback )
    {
        backgroundExecutor.execute( new Runnable()
        {
            @Override
            public void run()
            {
                Call<Token> call = networkService.login( loginWrapper );
                try
                {
                    Response<Token> execute = call.execute();
                    requestCallback.onSuccess( execute.body() );
                }
                catch ( Exception e )
                {
                    requestCallback.onFailed( new NetworkException( "Error en la autenticación", e ) );
                }
            }
        } );

    }

    @Override
    public void getParties(final RequestCallback<Map<Integer,Party>> requestCallback) {
        backgroundExecutor.execute( new Runnable()
        {
            @Override
            public void run()
            {
                Call<Map<Integer,Party>> call = networkService.getParties();
                try
                {
                    Response<Map<Integer,Party>> execute = call.execute();
                    requestCallback.onSuccess( execute.body() );
                }
                catch ( Exception e )
                {
                    requestCallback.onFailed( new NetworkException( null, e ) );
                }
            }
        } );
    }

    @Override
    public void addNewUser(final User user, final RequestCallback<User> requestCallback ) {

        backgroundExecutor.execute( new Runnable()
        {
            @Override
            public void run()
            {
                final User myUser= user;
                Call call = networkService.addUser(myUser);
                try
                {
                    Response<User> execute = call.execute();
                    requestCallback.onSuccess( execute.body() );
                }
                catch ( Exception e )
                {
                    requestCallback.onFailed( new NetworkException( "Error añadiendo usuario", e ) );
                }
            }
        } );
    }

}