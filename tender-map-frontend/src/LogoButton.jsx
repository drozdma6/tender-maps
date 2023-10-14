function LogoButton({href, imagePath, alt, imageStyle}){
    return (
        <a href={href} target="_blank" rel="noopener noreferrer">
            <img
                src={imagePath}
                alt={alt}
                style={{
                    width: "30px",
                    height: "30px",
                    margin: "15px 7px",
                    ...imageStyle}}
            />
        </a>
    )
}
export default LogoButton;