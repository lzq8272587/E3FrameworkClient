# E3FrameworkClient
This project includes the client library for the E3 transmission framework.

Basic usage：

        E3FrameworkClient e3client=E3FrameworkClient.getInstant(this);

        e3client.putStringRequest(new StringRequest("http://52.88.216.252/json_test.txt",0,"StringTest"),null);
        
        e3client.putObjectRequest(new ObjectRequest("http://52.88.216.252/boat.jpg",0,"ObjectTest"),null,null);
