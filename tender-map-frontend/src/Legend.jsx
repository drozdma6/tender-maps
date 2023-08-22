import './styles.css';

function Legend({title, text, items, children}){
    return (
        <div className="container">
            <h3>{title}</h3>
            <p>{text}</p>
            {items.map((item, index) => (
                <div key={index} className="legend-item">
                    <span style={{ backgroundColor: item.color }} className="legend-color"></span>
                    <span className="legend-label">{item.label}</span>
                </div>
            ))}
            {children}
        </div>
    );
}

export default Legend;





