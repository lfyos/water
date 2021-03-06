package kernel_information;

import kernel_engine.client_information;
import kernel_engine.interface_statistics;

public class statistics_interface_information extends jason_creator
{
	private client_information ci;
	
	public void print()
	{
		interface_statistics is=ci.statistics_interface;
		
		print("responsing_file_data_number",		is.responsing_file_data_number);
		print("responsing_network_data_number",		is.responsing_network_data_number);
		
		print("file_download_number",				is.file_download_number);
		print("file_download_data_compress_length",	is.file_download_data_compress_length);
		print("file_download_data_uncompress_length",is.file_download_data_uncompress_length);
		
		print("network_data_number",				is.network_data_number);
		print("network_data_compress_length",		is.network_data_compress_length);
		print("network_data_uncompress_length",		is.network_data_uncompress_length);
		
		print("proxy_request_file_number",			is.proxy_request_file_number);
		print("proxy_request_data_length",			is.proxy_request_data_length);
		print("proxy_response_file_number",			is.proxy_response_file_number);
		print("proxy_response_data_length",			is.proxy_response_data_length);
	}
	public statistics_interface_information(client_information my_ci)
	{
		super(my_ci.request_response);
		ci=my_ci;
	}
}
