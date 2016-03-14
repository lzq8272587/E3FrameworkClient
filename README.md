# E3FrameworkClient
This project includes the client library for the E3 transmission framework.

Basic usage：

        E3FrameworkClient e3client=E3FrameworkClient.getInstant(this);
        
        String url = "http://xxxxxxx";
        
        e3client.putByteRequest(new ByteRequest(url, ERequest.ACTIVE, "New Image Request"), new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                Log.d("onResponse","response:" + new String(response));
            }
        });

