package kernel_engine;

public class interface_statistics
{
	public volatile int  responsing_file_data_number;
	public volatile int  responsing_network_data_number;
	
	public volatile long file_download_number;
	public volatile long file_download_data_uncompress_length;
	public volatile long file_download_data_compress_length;
	
	public volatile long network_data_number;
	public volatile long network_data_uncompress_length;
	public volatile long network_data_compress_length;
	
	public volatile int proxy_request_file_number;
	public volatile long proxy_request_data_length;
	public volatile int proxy_response_file_number;
	public volatile long proxy_response_data_length;
	
	public void clear()
	{
		responsing_file_data_number=0;
		responsing_network_data_number=0;
		
		file_download_number=0;
		file_download_data_uncompress_length=0;
		file_download_data_compress_length=0;
		
		network_data_number=0;
		network_data_uncompress_length=0;
		network_data_compress_length=0;
		
		proxy_request_file_number=0;
		proxy_request_data_length=0;
		
		proxy_response_file_number=0;
		proxy_response_data_length=0;
	}
	public interface_statistics()
	{
		clear();
	}
}
