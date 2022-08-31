package kernel_engine;

public class proxy_information
{
	public String original_url;
	public String next_url;
	public boolean encode_flag;
	public String proxy_root_directory_name;
	public String compress_proxy_root_directory_name;
	public String proxy_charset_name;
	public long max_download_file_length;
	
	public proxy_information(
			String my_original_url,
			String my_next_url,boolean my_encode_flag,
			String my_proxy_root_directory_name,
			String my_compress_proxy_root_directory_name,
			String my_proxy_charset_name,
			long my_max_download_file_length)
	{
		original_url						=my_original_url;
		next_url							=my_next_url;
		encode_flag							=my_encode_flag;
		proxy_root_directory_name			=my_proxy_root_directory_name;
		compress_proxy_root_directory_name	=my_compress_proxy_root_directory_name;
		proxy_charset_name					=my_proxy_charset_name;
		max_download_file_length			=my_max_download_file_length;
	}
	public proxy_information(String my_original_url)
	{
		original_url						=my_original_url;
		next_url							=null;
		encode_flag							=false;
		proxy_root_directory_name			=null;
		compress_proxy_root_directory_name	=null;
		proxy_charset_name					=null;
		max_download_file_length			=0;
	}
}
